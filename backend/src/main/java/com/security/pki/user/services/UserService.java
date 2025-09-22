package com.security.pki.user.services;

import com.security.pki.shared.services.EmailService;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.exceptions.ActivationTokenAlreadyUsedException;
import com.security.pki.user.exceptions.ActivationTokenExpiredException;
import com.security.pki.user.exceptions.EmailAlreadyTakenException;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.ActivationToken;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;

    public RegistrationResponseDto register(RegistrationRequestDto request) {
        if (repository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyTakenException("This email is already taken. Please log in or activate your account via the email we sent you.");

        User user = mapper.fromRequest(request);
        user = repository.save(user);
        String token = activationTokenService.generateToken(user);
        emailService.sendVerificationEmail(request.getEmail(), token);
        return mapper.toRegistrationResponse(user);
    }

    public void activateUser(String token) {
        ActivationToken activationToken = activationTokenService.get(token);
        if (activationToken.getExpiresAt().isAfter(LocalDateTime.now())) {
            User user = activationToken.getUser();
            if (activationToken.isAlreadyUsed())
                throw new ActivationTokenAlreadyUsedException("Activation token has already been used. The account is already verified.");

            user.setVerified(true);
            repository.save(user);
            activationTokenService.markAsUsed(activationToken);
        } else {
            throw new ActivationTokenExpiredException("Activation token expired.");
        }
    }
}
