package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.GameRepository;
import de.echsecutables.rollandbuild.persistence.PlayerRepository;
import de.echsecutables.rollandbuild.utils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import java.util.List;
import java.util.Optional;

@RestController
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request. (Most likely problem: No ID or mal formatted ID given.)", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "User unknown.", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    private Player getOrCreatePlayer(String sessionId) {
        List<Player> players = playerRepository
                .findBySessionId(sessionId);
        if (!players.isEmpty()) {
            LOGGER.debug("Found record for player {}", players.get(0));
            return players.get(0);
        }
        Player player = playerRepository.save(new Player(sessionId));
        LOGGER.debug("Created new record for player {}", player);
        return player;
    }

    @GetMapping(value = "/player", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the data for the current user identified by session ID.")
    public ResponseEntity<Player> getPlayerData(HttpServletRequest request) {
        utils.logRequest(LOGGER, request);
        return ResponseEntity.ok(getOrCreatePlayer(request.getSession().getId()));
    }

    @PostMapping(value = "/player/name", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set the player name for the current session.")
    public ResponseEntity<GenericApiResponse> setPlayerName(@ApiParam(value = "New Player Name. Max 256 Characters, no weird stuff ;)", example = "Max Power", required = true)
                                                            @RequestBody String name,
                                                            HttpServletRequest request) {
        utils.logRequest(LOGGER, request);
        Validator validator = ESAPI.validator();
        if (!validator.isValidInput("playerName", name, "SafeString", 256, false)) {
            LOGGER.warn("Invalid Player Name '{}' from request {}", name, request.toString());
            return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, "Invalid Player Name", request.getRequestURI());
        }

        Player player = getOrCreatePlayer(request.getSession().getId());
        player.setName(name);
        player = playerRepository.save(player);
        LOGGER.debug("Changed name for Player: {}", player);
        return GenericApiResponse.buildResponse(HttpStatus.OK, "Name changed to '" + player.getName() + "'", request.getRequestURI());
    }

    @PostMapping(value = "/player/join", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create or join game.")
    public ResponseEntity<GenericApiResponse> joinGame(@ApiParam(value = "Game ID must be a positive Integer. Leave empty to create a new game.")
                                                       @RequestBody(required = false) String gameID,
                                                       HttpServletRequest request) {
        utils.logRequest(LOGGER, request);
        Game game;
        if (gameID == null || gameID.isEmpty()) {
            game = gameRepository.save(new Game());
            LOGGER.info("Created new Game {}", game);
        } else {
            Validator validator = ESAPI.validator();
            if (!validator.isValidNumber("gameID", gameID, 0, Long.MAX_VALUE, false)) {
                LOGGER.warn("Invalid Game ID '{}' from request {}", gameID, request.toString());
                return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, "Invalid Game ID", request.getRequestURI());
            }
            Optional<Game> optionalGame = gameRepository.findById(Long.parseLong(gameID));
            if (optionalGame.isEmpty()) {
                LOGGER.warn("Game not found: {}", gameID);
                return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, "Game ID '" + gameID + "' not found.", request.getRequestURI());
            }
            game = optionalGame.get();
        }

        Player player = getOrCreatePlayer(request.getSession().getId());
        if (player.getGames().contains(game)) {
            LOGGER.debug("Player {} already in Game {}", player, game);

            if (!game.getPlayers().contains(player)) {
                LOGGER.error("FIXME! Inconsistent state! Player {} not listed in game {}", player, game);
                game.getPlayers().add(player);
                game = gameRepository.save(game);
                LOGGER.debug("Fixed game {}", game);
            }
            return GenericApiResponse.buildResponse(HttpStatus.ALREADY_REPORTED, "Already playing in Game ID '" + gameID + "'.", request.getRequestURI());
        }
        player.getGames().add(game);
        player = playerRepository.save(player);

        game.getPlayers().add(player);
        gameRepository.save(game);

        LOGGER.info("Player {} joined Game {}", player, game);
        return GenericApiResponse.buildResponse(HttpStatus.OK, "Joined game " + game.getId(), request.getRequestURI());
    }

}
