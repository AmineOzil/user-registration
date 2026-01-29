package com.userapi.registration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * User entity representing a registered user in the system.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}