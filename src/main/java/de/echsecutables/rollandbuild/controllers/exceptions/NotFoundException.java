package de.echsecutables.rollandbuild.controllers.exceptions;

import de.echsecutables.rollandbuild.controllers.exceptions.PlayerNotInGameException;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// persistent object with given ID does not exist
@ResponseStatus(NOT_FOUND)
public class NotFoundException extends PlayerNotInGameException {
    public NotFoundException(String message) {
        super(message);
    }
}
