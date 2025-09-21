package com.security.pki.user.services;

import com.security.pki.shared.services.EmailService;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final EmailService emailService;

    public RegistrationResponseDto register(RegistrationRequestDto request) {
        User user = mapper.fromRequest(request);
        user = repository.save(user);
        String token = UUID.randomUUID().toString();
        emailService.sendVerificationEmail(request.getEmail(), token);
        return mapper.toRegistrationResponse(user);
    }
}
