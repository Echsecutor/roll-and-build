package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.FORBIDDEN;

// Game with given ID does not exist
@ResponseStatus(FORBIDDEN)
public class AgainstTheRulesException extends BadRequestException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AgainstTheRulesException(String message) {
        super(message);
    }
}
