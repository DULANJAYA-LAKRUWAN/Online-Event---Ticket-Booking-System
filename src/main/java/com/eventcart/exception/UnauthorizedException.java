package com.eventcart.exception;

/**
 * Thrown when an unauthenticated or unauthorized access attempt occurs.
 */
public class UnauthorizedException extends AppException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }
}
