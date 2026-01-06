package com.userapi.registration.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing user gender options.
 * Supports case-insensitive deserialization (e.g., "male", "MALE", "Male" are all valid).
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    @JsonValue
    public String toValue() {
        return this.name();
    }

    @JsonCreator
    public static Gender fromValue(String value) {
        if (value == null) {
            return null;
        }
        
        for (Gender gender : Gender.values()) {
            if (gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        
        throw new IllegalArgumentException("Invalid gender value: " + value + 
            ". Accepted values: MALE, FEMALE, OTHER (case insensitive)");
    }
}