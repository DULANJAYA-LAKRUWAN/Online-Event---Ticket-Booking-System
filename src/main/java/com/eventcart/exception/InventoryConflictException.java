package com.eventcart.exception;

/**
 * Thrown when ticket stock or availability conflicts with the requested operation (HTTP 409 Conflict).
 */
public class InventoryConflictException extends AppException {

    private static final long serialVersionUID = 1L;

    public InventoryConflictException(String message) {
        super(message);
    }

    public InventoryConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
