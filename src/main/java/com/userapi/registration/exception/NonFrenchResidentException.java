package com.userapi.registration.exception;

public class NonFrenchResidentException extends BusinessRuleException {
    
    private static final String CODE = "ERR_RULE_COUNTRY_FR";

    public NonFrenchResidentException() {
        super("Only French residents can register", CODE);
    }
}
