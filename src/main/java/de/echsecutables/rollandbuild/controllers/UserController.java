package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.Repositories;
import io.swagger.annotations.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Api
@RestController
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request.", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "User unknown.", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Repositories repositories;

    @GetMapping(value = "/player", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the data for the current user identified by session ID.")
    public ResponseEntity<Player> getPlayerData(HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        return ResponseEntity.ok(repositories.getOrCreatePlayer(request.getSession().getId()));
    }

    @PostMapping(value = "/player/name", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set the player name for the current session.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Name changed.", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> setPlayerName(@ApiParam(value = "New Player Name. Max 256 Characters, no weird stuff ;)", example = "Max Power", required = true)
                                                            @RequestBody String name,
                                                            HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        Validator validator = ESAPI.validator();
        if (!validator.isValidInput("playerName", name, "SafeString", 256, false)) {
            LOGGER.warn("Invalid Player Name '{}' from request {}", name, request.toString());
            return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, "Invalid Player Name", request.getRequestURI());
        }

        Player player = repositories.getOrCreatePlayer(request.getSession().getId());
        player.setName(name);
        player = repositories.save(player);
        LOGGER.debug("Changed name for Player: {}", player);
        return GenericApiResponse.buildResponse(HttpStatus.OK, "Name changed to '" + player.getName() + "'", request.getRequestURI());
    }

    @PostMapping(value = "/player/join", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create or join game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Existing game joined.", response = GenericApiResponse.class),
                    @ApiResponse(code = 201, message = "New game created and joined.", response = GenericApiResponse.class),
                    @ApiResponse(code = 208, message = "Player is already in game.", response = GenericApiResponse.class)
            }
    )
    public ResponseEntity<GenericApiResponse> joinGame(@ApiParam(value = "Game ID must be a positive Integer. Leave empty to create a new game.")
                                                       @RequestBody(required = false) String gameID,
                                                       HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        Game game;
        HttpStatus re = HttpStatus.OK;
        if (gameID == null || gameID.isEmpty()) {
            game = repositories.save(new Game());
            LOGGER.info("Created new Game {}", game);
            re = HttpStatus.CREATED;
        } else {
            Validator validator = ESAPI.validator();
            if (!validator.isValidNumber("gameID", gameID, 0, Long.MAX_VALUE, false)) {
                LOGGER.warn("Invalid Game ID '{}' from request {}", gameID, request.toString());
                return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, "Invalid Game ID", request.getRequestURI());
            }
            Optional<Game> optionalGame = repositories.loadGame(Long.parseLong(gameID));
            if (optionalGame.isEmpty()) {
                LOGGER.warn("Game not found: {}", gameID);
                return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "Game ID '" + gameID + "' not found.", request.getRequestURI());
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
            return GenericApiResponse.buildResponse(HttpStatus.ALREADY_REPORTED, "Already playing in Game ID '" + gameID + "'.", request.getRequestURI());
        }
        player.getGames().add(game);
        player = repositories.save(player);

        game.getPlayers().add(player);
        game = repositories.save(game);

        LOGGER.info("Player {} joined Game {}", player, game);
        return GenericApiResponse.buildResponse(re, "Joined game " + game.getId(), request.getRequestURI());
    }

}
