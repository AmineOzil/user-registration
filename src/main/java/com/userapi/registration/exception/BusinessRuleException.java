package com.userapi.registration.exception;

/**
 * Base exception for all business rule violations.
 */
public abstract class BusinessRuleException extends RuntimeException {
    
    private final String errorCode;

    protected BusinessRuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}