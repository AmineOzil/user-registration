package com.userapi.registration.service;

import com.userapi.registration.domain.policy.RegistrationPolicies;
import com.userapi.registration.dto.UserRegistrationRequest;
import com.userapi.registration.dto.UserResponse;
import com.userapi.registration.entity.User;
import com.userapi.registration.exception.NonFrenchResidentException;
import com.userapi.registration.exception.UserAlreadyExistsException;
import com.userapi.registration.exception.UserNotAdultException;
import com.userapi.registration.exception.UserNotFoundException;
import com.userapi.registration.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service handling user registration and retrieval operations.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Registers a new user after validating business rules.
     * 
     * @param request the registration request containing user data
     * @return the created user details
     * @throws UserNotAdultException if user is under 18 years old
     * @throws NonFrenchResidentException if user is not a French resident
     * @throws UserAlreadyExistsException if username already exists
     */
    @Transactional
    public UserResponse register(UserRegistrationRequest request) {
        validateBusinessRules(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .birthdate(request.getBirthdate())
                .countryOfResidence(request.getCountryOfResidence())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    /**
     * Retrieves user details by username.
     * 
     * @param username the username to search for
     * @return the user details
     * @throws UserNotFoundException if no user found with given username
     */
    @Transactional(readOnly = true)
    public UserResponse getUserDetails(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToResponse)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .birthdate(user.getBirthdate())
                .countryOfResidence(user.getCountryOfResidence())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .build();
    }

    private void validateBusinessRules(UserRegistrationRequest request) {
        validateAge(request.getBirthdate());
        validateCountry(request.getCountryOfResidence());
    }

    private void validateAge(LocalDate birthdate) {
        if (!RegistrationPolicies.isAdult(birthdate)) {
            throw new UserNotAdultException();
        }
    }

    private void validateCountry(String country) {
        if (!RegistrationPolicies.isFrenchResident(country)) {
            throw new NonFrenchResidentException();
        }
    }
}