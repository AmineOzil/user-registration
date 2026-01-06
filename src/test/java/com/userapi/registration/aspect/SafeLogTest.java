package com.userapi.registration.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SafeLogTest {

    @Test
    void toSafeArgs_shouldReturnEmptyArrayWhenNull() {
        assertThat(SafeLog.toSafeArgs(null)).isEqualTo("[]");
    }

    @Test
    void toSafeArgs_shouldReturnEmptyArrayWhenNoArgs() {
        assertThat(SafeLog.toSafeArgs(new Object[0])).isEqualTo("[]");
    }

    @Test
    void toSafeValue_shouldMaskPhoneNumberWhenPatternMatches() {
        String input = "UserResponse(username='bob', phoneNumber='0612345678')";

        String safe = SafeLog.toSafeValue(input);

        assertThat(safe).contains("phoneNumber='06***5678'");
    }

    @Test
    void toSafeValue_shouldReturnSameStringWhenNoPhoneNumberPresent() {
        String input = "UserResponse(username='bob')";

        assertThat(SafeLog.toSafeValue(input)).isEqualTo(input);
    }

    @Test
    void toSafeValue_shouldMaskShortPhoneWithStars() {
        String input = "UserResponse(username='bob', phoneNumber='123')";

        String safe = SafeLog.toSafeValue(input);

        assertThat(safe).contains("phoneNumber='***'");
    }

    @Test
    void toSafeValue_shouldHandleNullObject() {
        assertThat(SafeLog.toSafeValue(null)).isEqualTo("null");
    }

    @Test
    void toSafeArgs_shouldHandleSingleArg() {
        Object[] args = {"test"};
        assertThat(SafeLog.toSafeArgs(args)).isEqualTo("[test]");
    }

    @Test
    void toSafeArgs_shouldHandleMultipleArgs() {
        Object[] args = {"arg1", 123, "phoneNumber='0612345678'"};
        String result = SafeLog.toSafeArgs(args);
        assertThat(result).contains("arg1");
        assertThat(result).contains("123");
        assertThat(result).contains("06***5678");
    }
}
