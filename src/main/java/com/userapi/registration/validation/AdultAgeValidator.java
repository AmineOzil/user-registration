package com.userapi.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultAgeValidator implements ConstraintValidator<AdultAge, LocalDate> {

    private static final int MINIMUM_AGE = 18;

    @Override
    public boolean isValid(LocalDate birthdate, ConstraintValidatorContext context) {
        if (birthdate == null) {
            return false;
        }
        
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        return age >= MINIMUM_AGE;
    }
}