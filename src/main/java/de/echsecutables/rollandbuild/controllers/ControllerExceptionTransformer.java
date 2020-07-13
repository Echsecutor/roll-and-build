package de.echsecutables.rollandbuild.controllers;

import de.echsecutables.rollandbuild.controllers.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionTransformer extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionTransformer.class);

    @ExceptionHandler(BugFoundException.class)
    public ResponseEntity<GenericApiResponse> handleBugFoundException(BugFoundException ex, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Please report this error at https://github.com/Echsecutor/roll-and-build/issues \n " + ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GenericApiResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @ExceptionHandler(PlayerNotInGameException.class)
    public ResponseEntity<GenericApiResponse> handlePlayerNotInGameException(PlayerNotInGameException ex, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @ExceptionHandler(AgainstTheRulesException.class)
    public ResponseEntity<GenericApiResponse> handleAgainstTheRulesException(AgainstTheRulesException ex, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<GenericApiResponse> handleBadRequestException(BadRequestException ex, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    private ResponseEntity<Object> exceptionToGenericApiResponse(Exception ex, HttpStatus status, WebRequest request) {
        LOGGER.debug("Caught: {}", ex.toString());
        return GenericApiResponse.buildResponseObject(status, ex.getMessage(), ((ServletWebRequest) request).getRequest().getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return exceptionToGenericApiResponse(ex, status, request);
    }
}
