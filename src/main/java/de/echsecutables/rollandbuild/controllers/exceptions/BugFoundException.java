package de.echsecutables.rollandbuild.controllers.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

// Breach of contract encountered (most likely object in illegal state)
// this is always a FIX ME!
@ResponseStatus(INTERNAL_SERVER_ERROR)
public class BugFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public BugFoundException(String message) {
        super(message);
    }
}
