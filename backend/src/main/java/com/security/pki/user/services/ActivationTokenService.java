package com.security.pki.user.services;

import com.security.pki.user.models.ActivationToken;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.ActivationTokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivationTokenService {
    private final ActivationTokenRepository repository;

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();

        repository.save(activationToken);
        return token;
    }

    public ActivationToken get(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Activation token not found"));
    }

    public void markAsUsed(ActivationToken token) {
        token.setAlreadyUsed(Boolean.TRUE);
        repository.save(token);
    }

}
