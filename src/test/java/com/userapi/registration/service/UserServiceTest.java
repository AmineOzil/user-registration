package com.userapi.registration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.userapi.registration.dto.UserRegistrationRequest;
import com.userapi.registration.dto.UserResponse;
import com.userapi.registration.entity.Gender;
import com.userapi.registration.entity.User;
import com.userapi.registration.exception.NonFrenchResidentException;
import com.userapi.registration.exception.UserAlreadyExistsException;
import com.userapi.registration.exception.UserNotAdultException;
import com.userapi.registration.exception.UserNotFoundException;
import com.userapi.registration.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRegistrationRequest validRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validRequest = UserRegistrationRequest.builder()
                .username("amine.bou")
                .birthdate(LocalDate.of(2000, 1, 1))
                .countryOfResidence("France")
                .phoneNumber("0612345678")
                .gender(Gender.MALE)
                .build();

        savedUser = User.builder()
                .id(1L)
                .username("amine.bou")
                .birthdate(LocalDate.of(2000, 1, 1))
                .countryOfResidence("France")
                .phoneNumber("0612345678")
                .gender(Gender.MALE)
                .build();
    }

    @Test
    void register_shouldCreateUserSuccessfully() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("amine.bou");
        assertThat(response.getBirthdate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(response.getCountryOfResidence()).isEqualTo("France");
        assertThat(response.getPhoneNumber()).isEqualTo("0612345678");
        assertThat(response.getGender()).isEqualTo(Gender.MALE);

        verify(userRepository).existsByUsername("amine.bou");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowUserNotAdultException_whenUserIsUnder18() {
        validRequest.setBirthdate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(UserNotAdultException.class);

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowNonFrenchResidentException_whenCountryIsNotFrance() {
        validRequest.setCountryOfResidence("Germany");

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(NonFrenchResidentException.class);

        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowUserAlreadyExistsException_whenUsernameExists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(validRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("amine.bou");

        verify(userRepository).existsByUsername("amine.bou");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserDetails_shouldReturnUserSuccessfully() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(savedUser));

        UserResponse response = userService.getUserDetails("amine.bou");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("amine.bou");
        assertThat(response.getBirthdate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(response.getCountryOfResidence()).isEqualTo("France");
        assertThat(response.getPhoneNumber()).isEqualTo("0612345678");
        assertThat(response.getGender()).isEqualTo(Gender.MALE);

        verify(userRepository).findByUsername("amine.bou");
    }

    @Test
    void getUserDetails_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserDetails("nonexistent"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("nonexistent");

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void register_shouldAcceptExactly18YearsOld() {
        validRequest.setBirthdate(LocalDate.now().minusYears(18));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldAcceptFranceCaseInsensitive() {
        validRequest.setCountryOfResidence("france");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.register(validRequest);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldHandleNullOptionalFields() {
        // Given
        validRequest.setPhoneNumber(null);
        validRequest.setGender(null);
        
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser); // Mock return doesn't matter much here

        // When
        userService.register(validRequest);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertThat(capturedUser.getPhoneNumber()).isNull();
        assertThat(capturedUser.getGender()).isNull();
    }
}