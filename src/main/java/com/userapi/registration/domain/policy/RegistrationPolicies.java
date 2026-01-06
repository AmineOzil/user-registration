package com.userapi.registration.domain.policy;

import java.time.LocalDate;
import java.time.Period;

/**
 * User registration business rules.
 */
public class RegistrationPolicies {

    private static final int MINIMUM_AGE = 18;
    private static final String FRANCE = "France";

    public static boolean isAdult(LocalDate birthdate) {
        if (birthdate == null) {
            return false;
        }
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        return age >= MINIMUM_AGE;
    }

    public static boolean isFrenchResident(String countryOfResidence) {
        if (countryOfResidence == null || countryOfResidence.isBlank()) {
            return false;
        }
        return FRANCE.equalsIgnoreCase(countryOfResidence.trim());
    }

    public static int getMinimumAge() {
        return MINIMUM_AGE;
    }

    public static String getFrance() {
        return FRANCE;
    }
}