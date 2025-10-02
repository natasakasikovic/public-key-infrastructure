package com.security.pki.user.validators.compromised;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotCompromisedPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotCompromisedPassword {
    String message() default "This password is too common or has been compromised";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}