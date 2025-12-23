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
class ValidPhoneNumberValidatorTest {

    private ValidPhoneNumberValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidPhoneNumberValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0612345678", "+33612345678", "06123456789012", "1234567890"})
    @NullAndEmptySource
    void isValid_shouldReturnTrue_whenPhoneNumberIsValid(String phoneNumber) {
        boolean result = validator.isValid(phoneNumber, context);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"06abc45678", "061234567", "06 12 34 56 78", "06-12-34-56-78", "123"})
    void isValid_shouldReturnFalse_whenPhoneNumberIsInvalid(String phoneNumber) {
        boolean result = validator.isValid(phoneNumber, context);

        assertThat(result).isFalse();
    }
}