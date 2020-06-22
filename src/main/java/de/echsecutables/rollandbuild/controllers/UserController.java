package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.PlayerRepository;
import io.swagger.annotations.ApiOperation;
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
import java.util.Optional;

@RestController
@ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad request. (Most likely problem: No ID or mal formatted ID given.)", response = GenericApiResponse.class),
        @ApiResponse(code = 404, message = "User unknown.", response = GenericApiResponse.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Check the error message for details.", response = GenericApiResponse.class)
})
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericApiResponse.class);


    @Autowired
    private PlayerRepository playerRepository;

    private Player getOrCreatePlayer(String sessionId) {
        Optional<Player> optPlayer = playerRepository
                .findById(sessionId);
        if (optPlayer.isPresent()) {
            LOGGER.debug("Found record for player {}", optPlayer.get());
            return optPlayer.get();
        }
        Player player = playerRepository.save(new Player(sessionId));
        LOGGER.debug("Created new record for player {}", player);
        return player;
    }

    @GetMapping(value = "/player", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the data for the current user identified by session ID.")
    public ResponseEntity<Player> getPlayerData(HttpServletRequest request) {
        return ResponseEntity.ok(getOrCreatePlayer(request.getSession().getId()));
    }

    @PostMapping(value = "/player/name", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set the player name for the current session.")
    public ResponseEntity setPlayerName(@RequestBody String name, HttpServletRequest request) {
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

}
