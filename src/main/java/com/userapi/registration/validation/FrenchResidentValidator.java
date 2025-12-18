package com.userapi.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FrenchResidentValidator implements ConstraintValidator<FrenchResident, String> {

    private static final String FRANCE = "France";

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        if (country == null || country.isBlank()) {
            return false;
        }
        
        return FRANCE.equalsIgnoreCase(country.trim());
    }
}