package com.userapi.registration.service;

import com.userapi.registration.dto.UserRegistrationRequest;
import com.userapi.registration.dto.UserResponse;
import com.userapi.registration.entity.Gender;
import com.userapi.registration.exception.NonFrenchResidentException;
import com.userapi.registration.exception.UserAlreadyExistsException;
import com.userapi.registration.exception.UserNotAdultException;
import com.userapi.registration.exception.UserNotFoundException;
import com.userapi.registration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("amine.bou");
        validRequest.setBirthdate(LocalDate.of(2000, 1, 1));
        validRequest.setCountryOfResidence("France");
        validRequest.setPhoneNumber("0612345678");
        validRequest.setGender(Gender.MALE);
    }

    @Test
    void register_shouldCreateUser_whenAllBusinessRulesPass() {
        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername()).isEqualTo("amine.bou");
        assertThat(response.getBirthdate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(response.getCountryOfResidence()).isEqualTo("France");
        assertThat(response.getPhoneNumber()).isEqualTo("0612345678");
        assertThat(response.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    void register_shouldThrowUserNotAdultException_whenUserIsUnder18() {
        validRequest.setBirthdate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(UserNotAdultException.class)
                .hasMessageContaining("18");
    }

    @Test
    void register_shouldThrowNonFrenchResidentException_whenCountryIsNotFrance() {
        validRequest.setCountryOfResidence("Germany");

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(NonFrenchResidentException.class)
                .hasMessageContaining("French resident");
    }

    @Test
    void register_shouldThrowUserAlreadyExistsException_whenUsernameExists() {
        userService.register(validRequest);

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("amine.bou");
    }

    @Test
    void getUserDetails_shouldReturnUser_whenUserExists() {
        userService.register(validRequest);

        UserResponse response = userService.getUserDetails("amine.bou");

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("amine.bou");
    }

    @Test
    void getUserDetails_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        assertThatThrownBy(() -> userService.getUserDetails("nonexistent"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    void register_shouldAcceptExactly18YearsOld() {
        validRequest.setBirthdate(LocalDate.now().minusYears(18));

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
    }

    @Test
    void register_shouldAcceptFranceCaseInsensitive() {
        validRequest.setCountryOfResidence("france");

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getCountryOfResidence()).isEqualTo("france");
    }

    @Test
    void register_shouldAcceptOptionalPhoneNumber() {
        validRequest.setPhoneNumber(null);

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getPhoneNumber()).isNull();
    }
}
