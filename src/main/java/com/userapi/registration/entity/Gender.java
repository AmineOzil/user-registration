package com.userapi.registration.entity;

/**
 * Enum representing user gender options.
 * Case-insensitive JSON deserialization is handled by @JsonFormat in DTOs.
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER
}