package com.userapi.registration.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.userapi.registration.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for all controllers.
 * Translates exceptions into proper HTTP responses with consistent error format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CORRELATION_ID_KEY = "correlationId";

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        logger.warn("User already exists: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        logger.warn("User not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(
            BusinessRuleException ex, HttpServletRequest request) {
        logger.warn("Business rule violation [{}]: {}", ex.getErrorCode(), ex.getMessage());
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getErrorCode(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> validationErrors = extractValidationErrors(ex);
        logger.warn("Validation failed: {}: {}", request.getRequestURI(), validationErrors);

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.ERR_VALIDATION.getCode(),
                "Input validation failed",
                request,
                validationErrors
        );
    }

    /**
     * Handles JSON parsing errors from Jackson deserialization.
     * Provides user-friendly error messages for invalid formats, types, or enum values.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String message = "Malformed JSON request";
        String causeMessage = "Unknown";
        // Get the root cause of the exception
        Throwable specificCause = ex.getMostSpecificCause();
        
        if (specificCause != null && specificCause.getMessage() != null) {
            causeMessage = specificCause.getMessage().toLowerCase();

            // Analyze the root cause message to determine the type of error
            if (causeMessage.contains("localdate") || 
                causeMessage.contains("dayofmonth") || 
                causeMessage.contains("yearofera") ||
                causeMessage.contains("datetimeparseexception")) {
                message = "Invalid date format. Expected format: yyyy-MM-dd";
            } 
            else if (causeMessage.contains("gender") || causeMessage.contains("enum")) {
                message = "Invalid gender value. Accepted values: MALE, FEMALE, OTHER (case insensitive)";
            }
        }

        logger.warn("JSON parse error on {}: UserMessage='{}' | TechnicalCause='{}'", 
            request.getRequestURI(), 
            message, 
            causeMessage
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.ERR_JSON_PARSE.getCode(),
                message,
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.ERR_INTERNAL.getCode(),
                "An unexpected error occurred",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String errorCode,
                                                        String message,
                                                        HttpServletRequest request,
                                                        Map<String, String> validationErrors) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        errorResponse.setErrorCode(errorCode);
        errorResponse.setCorrelationId(MDC.get(CORRELATION_ID_KEY));

        if (validationErrors != null && !validationErrors.isEmpty()) {
            errorResponse.setValidationErrors(validationErrors);
        }

        return ResponseEntity.status(status).body(errorResponse);
    }

    private Map<String, String> extractValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            
            if (errorMessage == null) {
                errorMessage = "Validation failed";
            }
            
            validationErrors.merge(fieldName, errorMessage, (existing, newMsg) -> existing + "; " + newMsg);
        });
        return validationErrors;
    }
}