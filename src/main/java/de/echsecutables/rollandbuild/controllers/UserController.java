package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.Utils;
import de.echsecutables.rollandbuild.models.Player;
import de.echsecutables.rollandbuild.persistence.GamePlayRepositories;
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
    private GamePlayRepositories gamePlayRepositories;

    @GetMapping(value = "/player", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the data for the current user identified by session ID.")
    public ResponseEntity<Player> getPlayerData(HttpServletRequest request) {
        Utils.logRequest(LOGGER, request);
        return ResponseEntity.ok(gamePlayRepositories.getOrCreatePlayer(request.getSession().getId()));
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

        Player player = gamePlayRepositories.getOrCreatePlayer(request.getSession().getId());
        player.setName(name);
        player = gamePlayRepositories.save(player);
        LOGGER.debug("Changed name for Player: {}", player);
        return GenericApiResponse.buildResponse(HttpStatus.OK, "Name changed to '" + player.getName() + "'", request.getRequestURI());
    }


}
