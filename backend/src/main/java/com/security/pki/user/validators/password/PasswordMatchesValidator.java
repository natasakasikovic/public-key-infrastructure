package com.security.pki.user.validators.password;

import com.security.pki.user.dtos.RegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationRequestDto> {

    @Override
    public boolean isValid(RegistrationRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getPasswordConfirmation() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getPasswordConfirmation());
    }
}