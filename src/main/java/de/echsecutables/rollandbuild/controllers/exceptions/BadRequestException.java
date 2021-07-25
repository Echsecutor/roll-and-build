package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

// Some parameter in the request is missing/malformatted/out of range
@ResponseStatus(BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(String message) {
        super(message);
    }
}
