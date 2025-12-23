package com.userapi.registration.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class FrenchResidentValidatorTest {

    private FrenchResidentValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new FrenchResidentValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"France", "france", "FRANCE", "FrAnCe", "  France  "})
    @NullAndEmptySource
    void isValid_shouldReturnTrue_whenCountryIsFranceOrNullOrEmpty(String country) {
        boolean result = validator.isValid(country, context);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Germany", "Spain", "Italy"})
    void isValid_shouldReturnFalse_whenCountryIsNotFrance(String country) {
        boolean result = validator.isValid(country, context);

        assertThat(result).isFalse();
    }
}