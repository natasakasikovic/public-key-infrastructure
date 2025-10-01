package com.security.pki.user.mappers;

import com.security.pki.shared.models.PagedResponse;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.dtos.UserResponseDto;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

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

    public UserResponseDto toResponse(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public PagedResponse<UserResponseDto> toPagedResponse(Page<User> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
