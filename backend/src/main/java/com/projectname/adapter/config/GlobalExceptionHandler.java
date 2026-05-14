package com.projectname.adapter.config;

import com.projectname.adapter.inbound.controller.dto.ErrorResponseDto;
import com.projectname.adapter.inbound.controller.dto.ValidationErrorDto;
import com.projectname.adapter.inbound.controller.dto.ValidationErrorResponseDto;
import com.projectname.domain.exception.DomainException;
import com.projectname.domain.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;

/**
 * Global exception handler for the application.
 * Handles domain and validation exceptions with appropriate HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex      the exception
     * @param request the web request
     * @return error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(
            final ResourceNotFoundException ex, final WebRequest request) {
        final var error = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DomainException.
     *
     * @param ex      the exception
     * @param request the web request
     * @return error response with 400 status
     */
    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleDomainException(
            final DomainException ex, final WebRequest request) {
        final var error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException from domain validation.
     *
     * @param ex      the exception
     * @param request the web request
     * @return error response with 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            final IllegalArgumentException ex, final WebRequest request) {
        final var error = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors from @Valid annotations.
     *
     * @return error response with 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(
            final MethodArgumentNotValidException ex
    ) {

        final var errors = ex
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::mapError)
                .toList();

        final var error = ValidationErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .errors(errors)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    private ValidationErrorDto mapError(final ObjectError error) {

        if (error instanceof final FieldError fieldError) {
            return new ValidationErrorDto(
                    fieldError.getDefaultMessage(),
                    fieldError.getField()
            );
        }

        return new ValidationErrorDto(
                error.getDefaultMessage(),
                error.getObjectName()
        );
    }


    /**
     * Handles generic exceptions.
     *
     * @param ex      the exception
     * @param request the web request
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDto> handleGenericException(final Exception ex, final WebRequest request) {
        final var error = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An internal server error occurred")
                .timestamp(OffsetDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
