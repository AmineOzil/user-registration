package com.userapi.registration.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * User entity representing a registered user in the system.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username - must be unique
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * User's birthdate - used to verify age requirement (18+)
     */
    @Column(nullable = false)
    private LocalDate birthdate;

    /**
     * Country of residence - must be France for registration
     */
    @Column(nullable = false, length = 100)
    private String countryOfResidence;

    /**
     * Phone number - optional field
     */
    @Column(length = 20)
    private String phoneNumber;

    /**
     * Gender - optional field
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    public User() {
    }

    public User(String username, LocalDate birthdate, String countryOfResidence) {
        this.username = username;
        this.birthdate = birthdate;
        this.countryOfResidence = countryOfResidence;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthdate=" + birthdate +
                ", countryOfResidence='" + countryOfResidence + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}