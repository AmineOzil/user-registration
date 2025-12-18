package com.userapi.registration.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdultAgeValidator.class)
@Documented
public @interface AdultAge {
    
    String message() default "User must be at least 18 years old";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}