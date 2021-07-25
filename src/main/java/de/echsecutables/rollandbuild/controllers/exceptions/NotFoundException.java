package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// persistent object with given ID does not exist
@ResponseStatus(NOT_FOUND)
public class NotFoundException extends PlayerNotInGameException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }
}
