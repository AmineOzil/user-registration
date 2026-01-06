package com.userapi.registration.exception;

public class UserNotFoundException extends BusinessRuleException {
    
    private static final String CODE = "ERR_USER_NOT_FOUND";

    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found", CODE);
    }
}