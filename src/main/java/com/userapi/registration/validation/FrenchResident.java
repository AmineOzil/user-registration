package com.userapi.registration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FrenchResidentValidator.class)
@Documented
public @interface FrenchResident {
    
    String message() default "Only French residents are allowed to create an account";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}