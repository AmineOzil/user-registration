package com.userapi.registration.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class GenderTest {

    @Test
    void fromValue_shouldReturnNull_whenValueIsNull() {
        assertThat(Gender.fromValue(null)).isNull();
    }

    @Test
    void fromValue_shouldReturnGender_whenValueIsValid() {
        assertThat(Gender.fromValue("MALE")).isEqualTo(Gender.MALE);
        assertThat(Gender.fromValue("male")).isEqualTo(Gender.MALE);
        assertThat(Gender.fromValue("Male")).isEqualTo(Gender.MALE);
    }

    @Test
    void fromValue_shouldThrowException_whenValueIsInvalid() {
        assertThatThrownBy(() -> Gender.fromValue("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid gender value");
    }

    @Test
    void toValue_shouldReturnUpperCaseName() {
        assertThat(Gender.MALE.toValue()).isEqualTo("MALE");
        assertThat(Gender.FEMALE.toValue()).isEqualTo("FEMALE");
        assertThat(Gender.OTHER.toValue()).isEqualTo("OTHER");
    }
}
