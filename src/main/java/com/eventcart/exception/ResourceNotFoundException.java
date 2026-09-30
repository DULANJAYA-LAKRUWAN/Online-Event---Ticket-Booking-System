package com.eventcart.exception;

/**
 * Thrown when an expected entity/resource is not found.
 */
public class ResourceNotFoundException extends AppException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }
}
