package com.userapi.registration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneNumberValidator.class)
@Documented
public @interface ValidPhoneNumber {
    
    String message() default "Phone number must contain only digits with optional + sign and at least 10 digits";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}