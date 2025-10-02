package com.security.pki.user.services;

import com.security.pki.shared.models.PagedResponse;
import com.security.pki.shared.services.EmailService;
import com.security.pki.shared.services.LoggerService;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.dtos.UserResponseDto;
import com.security.pki.user.enums.Role;
import com.security.pki.user.exceptions.ActivationTokenAlreadyUsedException;
import com.security.pki.user.exceptions.ActivationTokenExpiredException;
import com.security.pki.user.exceptions.EmailAlreadyTakenException;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.ActivationToken;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final LoggerService loggerService;

    public RegistrationResponseDto register(RegistrationRequestDto request, Role role) {
        if (Boolean.TRUE.equals(repository.existsByEmail(request.getEmail())))
            throw new EmailAlreadyTakenException("This email is already taken. Please log in or activate your account via the email we sent you.");

        User user = mapper.fromRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        repository.save(user);
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

    public PagedResponse<UserResponseDto> getUsers(Pageable pageable) {
        return mapper.toPagedResponse(repository.findAll(pageable));
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found with id: %s", id)));
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
}
