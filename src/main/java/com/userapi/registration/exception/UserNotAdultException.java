package com.userapi.registration.exception;

public class UserNotAdultException extends BusinessRuleException {
    
    private static final String CODE = "ERR_RULE_AGE_MIN";

    public UserNotAdultException() {
        super("User must be at least 18 years old", CODE);
    }
}