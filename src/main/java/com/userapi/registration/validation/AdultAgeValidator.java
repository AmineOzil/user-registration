package com.userapi.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

import com.userapi.registration.domain.policy.RegistrationPolicies;

public class AdultAgeValidator implements ConstraintValidator<AdultAge, LocalDate> {

    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {
        return RegistrationPolicies.isAdult(birthdate);
    }
}