package com.security.pki.user.validators.compromised;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class NotCompromisedPasswordValidator implements ConstraintValidator<NotCompromisedPassword, String> {

    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "12345678", "123456789", "qwerty", "abc123", "11111111", "123123123", "adfghjk"
    );

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return !COMMON_PASSWORDS.contains(password.toLowerCase());
    }
}