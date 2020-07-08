package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

// Game with given ID does not exist
@ResponseStatus(FORBIDDEN)
public class AgainstTheRulesException extends BadRequestException {
    public AgainstTheRulesException(String message) {
        super(message);
    }
}
