package com.eventcart.exception;

/**
 * Thrown when business validation fails.
 */
public class ValidationException extends AppException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
