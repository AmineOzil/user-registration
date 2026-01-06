package com.userapi.registration.exception;

public class UserAlreadyExistsException extends BusinessRuleException {
    
    private static final String CODE = "ERR_USER_ALREADY_EXISTS";

    public UserAlreadyExistsException(String username) {
        super("User with username '" + username + "' already exists", CODE);
    }
}