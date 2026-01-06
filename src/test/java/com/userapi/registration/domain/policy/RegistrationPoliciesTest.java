package com.userapi.registration.domain.policy;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RegistrationPoliciesTest {

    @ParameterizedTest
    @ValueSource(ints = {18, 19, 25, 30, 65})
    void isAdult_shouldReturnTrue_whenAgeIsAtLeast18(int years) {
        LocalDate birthdate = LocalDate.now().minusYears(years);

        boolean result = RegistrationPolicies.isAdult(birthdate);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 17})
    void isAdult_shouldReturnFalse_whenAgeIsUnder18(int years) {
        LocalDate birthdate = LocalDate.now().minusYears(years);

        boolean result = RegistrationPolicies.isAdult(birthdate);

        assertThat(result).isFalse();
    }

    @Test
    void isAdult_shouldReturnFalse_when18YearsMinusOneDay() {
        LocalDate birthdate = LocalDate.now().minusYears(18).plusDays(1);

        boolean result = RegistrationPolicies.isAdult(birthdate);

        assertThat(result).isFalse();
    }

    @Test
    void isAdult_shouldReturnFalse_whenBirthdateIsNull() {
        boolean result = RegistrationPolicies.isAdult(null);

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"France", "france", "FRANCE", "FrAnCe", "  France  "})
    void isFrenchResident_shouldReturnTrue_whenCountryIsFrance(String country) {
        boolean result = RegistrationPolicies.isFrenchResident(country);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Germany", "Spain", "Italy", "   "})
    @NullAndEmptySource
    void isFrenchResident_shouldReturnFalse_whenCountryIsNotFrance(String country) {
        boolean result = RegistrationPolicies.isFrenchResident(country);

        assertThat(result).isFalse();
    }

    @Test
    void getMinimumAge_shouldReturn18() {
        int minimumAge = RegistrationPolicies.getMinimumAge();

        assertThat(minimumAge).isEqualTo(18);
    }

    @Test
    void getFrance_shouldReturnFrance() {
        String france = RegistrationPolicies.getFrance();

        assertThat(france).isEqualTo("France");
    }
}