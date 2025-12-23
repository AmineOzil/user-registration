package com.userapi.registration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class AdultAgeValidatorTest {

    private AdultAgeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new AdultAgeValidator();
    }

    @Test
    void isValid_shouldReturnTrue_whenAgeIs18() {
        LocalDate birthdate = LocalDate.now().minusYears(18);

        boolean result = validator.isValid(birthdate, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_shouldReturnTrue_whenAgeIsOver18() {
        LocalDate birthdate = LocalDate.now().minusYears(25);

        boolean result = validator.isValid(birthdate, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenAgeIsUnder18() {
        LocalDate birthdate = LocalDate.now().minusYears(17);

        boolean result = validator.isValid(birthdate, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_when18YearsMinusOneDay() {
        LocalDate birthdate = LocalDate.now().minusYears(18).plusDays(1);

        boolean result = validator.isValid(birthdate, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_shouldReturnTrue_whenBirthdateIsNull() {
        boolean result = validator.isValid(null, context);

        assertThat(result).isTrue();
    }
}