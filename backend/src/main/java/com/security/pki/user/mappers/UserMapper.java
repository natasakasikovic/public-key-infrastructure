package com.security.pki.user.mappers;

import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.modelmapper.ModelMapper;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public User fromRequest(RegistrationRequestDto request) {
        return modelMapper.map(request, User.class);
    }

    public RegistrationResponseDto toRegistrationResponse(User user) {
        String message = "Activation link sent to email " + user.getEmail() + ". Please check your inbox.";
        return new RegistrationResponseDto(message);
    }
}
