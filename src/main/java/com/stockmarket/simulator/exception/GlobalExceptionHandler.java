package com.stockmarket.simulator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler for REST API.
 * <p>
 * Maps application exceptions to HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles resource not found errors.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error(ex.getMessage(), 404));
    }

    /**
     * Handles invalid request errors.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequest(BadRequestException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error(ex.getMessage(), 400));
    }

    /**
     * Handles unexpected errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Internal error", 500));
    }

    /**
     * Builds standard error response body.
     */
    private Map<String, Object> error(String message, int status) {
        return Map.of("timestamp", Instant.now().toString(), "status", status, "message", message);
    }
}
