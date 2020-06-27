package de.echsecutables.rollandbuild;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class utils {
    public static void logRequest(Logger logger, HttpServletRequest request) {
        logger.trace("[" + request.getSession().getId() + "] " + request.getMethod() + ": " + request.getRequestURI());
    }
}
