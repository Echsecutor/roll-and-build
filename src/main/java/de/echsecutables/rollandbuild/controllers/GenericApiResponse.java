package de.echsecutables.rollandbuild.controllers;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "ApiResponse", description = "Generic response including a message for debugging errors.")
public class GenericApiResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericApiResponse.class);

    @ApiModelProperty(value = "Timestamp of response generation.")
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "Same code as returned in the HTTP header. Repeated here for logging/debugging purposes.", example = "200")
    private HttpStatus status;

    @ApiModelProperty(value = "Specific error message. Required if the status is not 2xx.", example = "Write-only-memory subsystem too slow for this machine.")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(value = "The requested path.", required = true, example = "/")
    private String path;

    private GenericApiResponse(HttpStatus status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.status = status;
        this.message = message;
    }

    @JsonGetter("status")
    public int getTheStatus() {
        return status.value();
    }

    @ApiModelProperty(value = "Standard human readable reason for the HTTP status code.", example = "Ok")
    @JsonGetter("reason")
    public String getTheReason() {
        return status.getReasonPhrase();
    }


    public static ResponseEntity<GenericApiResponse> buildResponse(HttpStatus status, String message, String path) {
        LOGGER.debug("Building generic API response : ({}, {}, {})", status.toString(), message, path);
        return new ResponseEntity<GenericApiResponse>(new GenericApiResponse(status, message, path), status);
    }

    public static ResponseEntity<GenericApiResponse> responseFromStringResponse(ResponseEntity<String> stringResponse, String path) {
        return buildResponse(stringResponse.getStatusCode(), stringResponse.getBody(), path);
    }

}