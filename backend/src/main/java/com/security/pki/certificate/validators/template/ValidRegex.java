package com.security.pki.certification.validators.template;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegexValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegex {
    String message() default "Invalid regex pattern";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
