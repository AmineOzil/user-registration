package com.userapi.registration.controller;

import com.userapi.registration.dto.UserRegistrationRequest;
import com.userapi.registration.dto.UserResponse;
import com.userapi.registration.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user registration and retrieval operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     * 
     * @param request the registration request with user data
     * @return 201 Created with the created user details
     */
    @PostMapping
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves user details by username.
     * 
     * @param username the username to search for
     * @return 200 OK with user details
     */
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserDetails(@PathVariable String username) {
        UserResponse response = userService.getUserDetails(username);
        return ResponseEntity.ok(response);
    }
}