package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

// The current player is not playing in the provided gameId
@ResponseStatus(BAD_REQUEST)
public class PlayerNotInGameException extends BadRequestException {
    public PlayerNotInGameException(String message) {
        super(message);
    }
}
