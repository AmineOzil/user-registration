package com.userapi.registration.validation;

import com.userapi.registration.domain.policy.RegistrationPolicies;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FrenchResidentValidator implements ConstraintValidator<FrenchResident, String> {

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        if (country == null || country.isBlank()) {
            return true;
        }
        return RegistrationPolicies.isFrenchResident(country);
    }
}