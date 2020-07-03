package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.models.Game;
import de.echsecutables.rollandbuild.models.GameConfig;
import de.echsecutables.rollandbuild.models.Phase;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.Repositories;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    Repositories repositories;

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Config Accepted, game phase advanced.", response = GenericApiResponse.class),
            @ApiResponse(code = 403, message = "Player not allowed to set config for this game now.", response = GenericApiResponse.class)
    })
    @PostMapping(value = "/game/{gameId}/setConfig", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiResponse> setGameConfig(
            @PathVariable("gameId") Long gameId,
            @RequestBody GameConfig gameConfig,
            HttpServletRequest request
    ) {
        LOGGER.debug(request.getRequestURI() + " called with " + gameConfig);

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
}
