package com.userapi.registration.exception;

public enum ErrorCode {
    ERR_VALIDATION("ERR_VALIDATION"),
    ERR_JSON_PARSE("ERR_JSON_PARSE"),
    ERR_USER_ALREADY_EXISTS("ERR_USER_ALREADY_EXISTS"),
    ERR_USER_NOT_FOUND("ERR_USER_NOT_FOUND"),
    ERR_RULE_AGE_MIN("ERR_RULE_AGE_MIN"),
    ERR_RULE_COUNTRY_FR("ERR_RULE_COUNTRY_FR"),
    ERR_INTERNAL("ERR_INTERNAL");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
