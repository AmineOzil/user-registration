package com.userapi.registration.controller;

import tools.jackson.databind.ObjectMapper;
import com.userapi.registration.dto.UserRegistrationRequest;
import com.userapi.registration.entity.Gender;
import com.userapi.registration.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class UserControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();

        validRequest = new UserRegistrationRequest();
        validRequest.setUsername("amine.bou");
        validRequest.setBirthdate(LocalDate.of(2000, 1, 1));
        validRequest.setCountryOfResidence("France");
        validRequest.setPhoneNumber("0612345678");
        validRequest.setGender(Gender.MALE);
    }

    @Test
    void register_shouldReturn201_whenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .header("X-Correlation-Id", "test-123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("amine.bou"))
                .andExpect(jsonPath("$.birthdate").value("2000-01-01"))
                .andExpect(jsonPath("$.countryOfResidence").value("France"))
                .andExpect(jsonPath("$.phoneNumber").value("0612345678"))
                .andExpect(jsonPath("$.gender").value("MALE"));
    }

    @Test
    void register_shouldReturn400_whenUsernameIsMissing() throws Exception {
        validRequest.setUsername(null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_VALIDATION"))
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }

    @Test
    void register_shouldReturn400_whenUsernameIsTooShort() throws Exception {
        validRequest.setUsername("ab");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_VALIDATION"))
                .andExpect(jsonPath("$.validationErrors.username").value(containsString("between 3 and 50")));
    }

    @Test
    void register_shouldReturn400_whenBirthdateIsInvalidFormat() throws Exception {
        String invalidJson = """
                {
                    "username": "amine.bou",
                    "birthdate": "invalid-date",
                    "countryOfResidence": "France",
                    "phoneNumber": "0612345678",
                    "gender": "MALE"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_JSON_PARSE"));
    }

    @Test
    void register_shouldReturn400_whenGenderIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "username": "amine.bou",
                    "birthdate": "2000-01-01",
                    "countryOfResidence": "France",
                    "phoneNumber": "0612345678",
                    "gender": "INVALID"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_JSON_PARSE"))
                .andExpect(jsonPath("$.message").value(containsString("gender")));
    }

    @Test
    void register_shouldReturn400_whenPhoneNumberIsInvalid() throws Exception {
        validRequest.setPhoneNumber("invalid");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("ERR_VALIDATION"))
                .andExpect(jsonPath("$.validationErrors.phoneNumber").exists());
    }

    @Test
    void register_shouldReturn422_whenUserIsUnder18() throws Exception {
        validRequest.setBirthdate(LocalDate.now().minusYears(17));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.errorCode").value("ERR_RULE_AGE_MIN"))
                .andExpect(jsonPath("$.message").value(containsString("18")));
    }

    @Test
    void register_shouldReturn422_whenCountryIsNotFrance() throws Exception {
        validRequest.setCountryOfResidence("Germany");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.errorCode").value("ERR_RULE_COUNTRY_FR"))
                .andExpect(jsonPath("$.message").value(containsString("French resident")));
    }

    @Test
    void register_shouldReturn409_whenUsernameAlreadyExists() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

        // Try to register again with same username
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("ERR_USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(containsString("amine.bou")));
    }

    @Test
    void getUserDetails_shouldReturn200_whenUserExists() throws Exception {
        // Register user first
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

        // Get user details
        mockMvc.perform(get("/api/users/{username}", "amine.bou")
                        .header("X-Correlation-Id", "test-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("amine.bou"))
                .andExpect(jsonPath("$.birthdate").value("2000-01-01"))
                .andExpect(jsonPath("$.countryOfResidence").value("France"))
                .andExpect(jsonPath("$.phoneNumber").value("0612345678"))
                .andExpect(jsonPath("$.gender").value("MALE"));
    }

    @Test
    void getUserDetails_shouldReturn404_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/users/{username}", "nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("ERR_USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value(containsString("nonexistent")));
    }

    @Test
    void register_shouldAcceptExactly18YearsOld() throws Exception {
        validRequest.setBirthdate(LocalDate.now().minusYears(18));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_shouldAcceptFranceCaseInsensitive() throws Exception {
        validRequest.setCountryOfResidence("france");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.countryOfResidence").value("france"));
    }

    @Test
    void register_shouldAcceptGenderCaseInsensitive() throws Exception {
        String jsonWithLowercase = """
                {
                    "username": "jane.doe",
                    "birthdate": "2000-01-01",
                    "countryOfResidence": "France",
                    "phoneNumber": "0612345678",
                    "gender": "female"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithLowercase))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gender").value("FEMALE"));
    }

    @Test
    void register_shouldAcceptOptionalPhoneNumber() throws Exception {
        validRequest.setPhoneNumber(null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.phoneNumber").isEmpty());
    }
}