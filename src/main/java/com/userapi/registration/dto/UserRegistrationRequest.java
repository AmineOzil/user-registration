package com.userapi.registration.dto;

import com.userapi.registration.entity.Gender;
import com.userapi.registration.validation.AdultAge;
import com.userapi.registration.validation.FrenchResident;
import com.userapi.registration.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotNull(message = "Birthdate is required")
    @AdultAge
    private LocalDate birthdate;

    @NotBlank(message = "Country of residence is required")
    @FrenchResident
    private String countryOfResidence;

    @ValidPhoneNumber
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    private Gender gender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}