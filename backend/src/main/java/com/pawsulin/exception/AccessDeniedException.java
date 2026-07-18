package com.pawsulin.exception;

/**
 * Exception thrown when a user does not have sufficient access to perform an operation.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
