package com.userapi.registration.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class GenderTest {

    @Test
    void enumValues_shouldHaveCorrectNames() {
        assertThat(Gender.MALE.name()).isEqualTo("MALE");
        assertThat(Gender.FEMALE.name()).isEqualTo("FEMALE");
        assertThat(Gender.OTHER.name()).isEqualTo("OTHER");
    }

    @Test
    void valueOf_shouldReturnCorrectEnum() {
        assertThat(Gender.valueOf("MALE")).isEqualTo(Gender.MALE);
        assertThat(Gender.valueOf("FEMALE")).isEqualTo(Gender.FEMALE);
        assertThat(Gender.valueOf("OTHER")).isEqualTo(Gender.OTHER);
    }
}
