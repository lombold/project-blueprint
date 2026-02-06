package com.gymbuddy.adapter.config;

import com.gymbuddy.domain.exception.DomainException;
import com.gymbuddy.domain.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the application.
 * Handles domain and validation exceptions with appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles ResourceNotFoundException.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response with 404 status
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, WebRequest request) {
    ErrorResponse error =
        ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  /**
   * Handles DomainException.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response with 400 status
   */
  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleDomainException(
      DomainException ex, WebRequest request) {
    ErrorResponse error =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles IllegalArgumentException from domain validation.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response with 400 status
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    ErrorResponse error =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles generic exceptions.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response with 500 status
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    ErrorResponse error =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("An internal server error occurred")
            .timestamp(LocalDateTime.now())
            .build();
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Error response DTO. */
  @lombok.Getter
  @lombok.Setter
  @lombok.AllArgsConstructor
  @lombok.Builder
  static class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
  }
}
