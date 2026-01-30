package com.MicroserviciosSpringBoot2025.Product.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. Error 404: Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    // 2. Error 400: For Enums (Country or Currency) that do not match
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        String message = "Invalid data: Verify that the country or currency code is correct.";
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request, null);
    }

    // 3. Error 400: Bean Validation (@NotBlank, @Positive...)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        // Use the helper to maintain the structure
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                errors
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    // 4. Error 500: Any other unexpected error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), request, null);
    }

    // --- HELPER METHOD TO AVOID CODE REPETITION ---
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request, List<String> details) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                LocalDateTime.now(),
                message,
                request.getDescription(false),
                details
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
