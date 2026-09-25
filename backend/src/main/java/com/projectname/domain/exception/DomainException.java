package com.projectname.domain.exception;

/**
 * Exception thrown when a domain invariant is violated.
 */
public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
