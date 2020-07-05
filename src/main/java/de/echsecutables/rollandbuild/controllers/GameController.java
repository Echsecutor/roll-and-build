package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.GameConfig;
import de.echsecutables.rollandbuild.models.Phase;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.RepositoryWrapper;
import io.swagger.annotations.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@Api
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request. (Most likely problem: No ID or mal formatted ID given.)", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "Game unknown.", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    @Autowired
    RepositoryWrapper repositories;


    @SuppressWarnings("rawtypes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game Data attached", response = Game.class)
    })
    @PostMapping(value = "/game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getGame(
            @PathVariable("gameId") Long gameId,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        Optional<Game> optionalGame = repositories.loadGame(gameId);
        if (optionalGame.isEmpty()) {
            return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "GameId " + gameId.toString() + " not found.", request.getRequestURI());
        }
        return ResponseEntity.ok(optionalGame.get());
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Config Accepted, game phase advanced.", response = GenericApiResponse.class),
            @ApiResponse(code = 403, message = "Player not allowed to set config for this game now.", response = GenericApiResponse.class)
    })
    @PostMapping(value = "/game/setConfig/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> setGameConfig(
            @PathVariable("gameId") Long gameId,
            @RequestBody GameConfig gameConfig,
            HttpServletRequest request
    ) {
        Utils.logRequest(LOGGER, request);
        LOGGER.debug("Config {} ", gameConfig);

        Optional<Game> optionalGame = repositories.loadGame(gameId);
        if (optionalGame.isEmpty()) {
            return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "GameId " + gameId.toString() + " not found.", request.getRequestURI());
        }
        Game game = optionalGame.get();
        if (game.getPhase() != Phase.NOT_READY) {
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "Game " + game.getId() + " is already in phase " + game.getPhase(), request.getRequestURI());
        }

        Player player = repositories.getOrCreatePlayer(request.getSession().getId());
        if (!game.getPlayers().contains(player)) {
            return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, "Player " + player.getId() + " not in game " + game.getId(), request.getRequestURI());
        }

        LOGGER.debug("Setting Config {} for game {}", gameConfig, game);

        game.setGameConfig(gameConfig);
        game.setPhase(Phase.READY);
        game = repositories.save(game);
        LOGGER.info("New game config set: {}", game);

        return GenericApiResponse.buildResponse(HttpStatus.OK, "Config accepted, Phase advanced to " + game.getPhase(), request.getRequestURI());
    }

    @PostMapping(value = "/game/join", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a new game game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "New game created and joined. Message = gameId", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> createGame(HttpServletRequest request) {
        return joinGame(null, request);
    }

    @PostMapping(value = "/game/join/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create or join game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Existing game joined.", response = GenericApiResponse.class),
                    @ApiResponse(code = 208, message = "Player is already in game.", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> joinGame(@ApiParam(value = "Game ID must be a positive Integer")
                                                       @PathVariable(name = "gameId", value = "gameId") String gameId,
                                                       HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        Game game;
        HttpStatus re = HttpStatus.OK;
        if (gameId == null || gameId.isEmpty()) {
            game = repositories.save(new Game());
            LOGGER.info("Created new Game {}", game);
            re = HttpStatus.CREATED;
        } else {
            Validator validator = ESAPI.validator();
            if (!validator.isValidNumber("gameId", gameId, 0, Long.MAX_VALUE, false)) {
                LOGGER.warn("Invalid Game ID '{}' from request {}", gameId, request.toString());
                return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, "Invalid Game ID", request.getRequestURI());
            }
            Optional<Game> optionalGame = repositories.loadGame(Long.parseLong(gameId));
            if (optionalGame.isEmpty()) {
                LOGGER.warn("Game not found: {}", gameId);
                return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "Game ID '" + gameId + "' not found.", request.getRequestURI());
            }
            game = optionalGame.get();
        }

        Player player = repositories.getOrCreatePlayer(request.getSession().getId());
        if (player.getGames().contains(game)) {
            LOGGER.debug("Player {} already in Game {}", player, game);

            if (!game.getPlayers().contains(player)) {
                LOGGER.error("FIXME! Inconsistent state! Player {} not listed in game {}", player, game);
                game.getPlayers().add(player);
                game = repositories.save(game);
                LOGGER.debug("Fixed game {}", game);
            }
            return GenericApiResponse.buildResponse(HttpStatus.ALREADY_REPORTED, "Already playing in Game ID '" + gameId + "'.", request.getRequestURI());
        }
        player.getGames().add(game);
        player = repositories.save(player);

        game.getPlayers().add(player);
        game = repositories.save(game);

        LOGGER.info("Player {} joined Game {}", player, game);
        return GenericApiResponse.buildResponse(re, game.getId().toString(), request.getRequestURI());
    }
}
