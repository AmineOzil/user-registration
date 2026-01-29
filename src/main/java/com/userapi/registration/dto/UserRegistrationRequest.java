package com.userapi.registration.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.userapi.registration.entity.Gender;
import com.userapi.registration.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Birthdate is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @NotBlank(message = "Country of residence is required")
    private String countryOfResidence;

    @ValidPhoneNumber
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES)
    private Gender gender;
}