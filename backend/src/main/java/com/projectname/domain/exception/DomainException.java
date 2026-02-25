package com.projectname.domain.exception;

/**
 * Exception thrown when a domain invariant is violated.
 */
public class DomainException extends RuntimeException {

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
