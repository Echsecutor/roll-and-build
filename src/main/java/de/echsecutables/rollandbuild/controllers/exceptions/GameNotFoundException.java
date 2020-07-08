package de.echsecutables.rollandbuild.controllers.exceptions;

import de.echsecutables.rollandbuild.controllers.exceptions.PlayerNotInGameException;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// Game with given ID does not exist
@ResponseStatus(NOT_FOUND)
public class GameNotFoundException extends PlayerNotInGameException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
