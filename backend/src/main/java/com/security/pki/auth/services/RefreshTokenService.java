package com.security.pki.auth.services;

import com.security.pki.auth.dtos.LoginRequestDto;
import com.security.pki.auth.dtos.LoginResponseDto;
import com.security.pki.auth.dtos.RefreshTokenRequestDto;
import com.security.pki.auth.exceptions.InvalidRefreshTokenException;
import com.security.pki.auth.models.RefreshToken;
import com.security.pki.auth.repositories.RefreshTokenRepository;
import com.security.pki.security.utils.JwtUtil;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-token.expiration:604800000}") // 7 days default
    private long refreshTokenExpiration;
    private final JwtUtil jwtUtil;

    public LoginResponseDto refreshToken(RefreshTokenRequestDto request) {
        String oldToken = request.getToken();
        RefreshToken refreshToken = repository.findByToken(oldToken)
                .map(this::verifyExpiration)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired."));

        User user = refreshToken.getUser();
        revokeToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().toString(), user.getId());
        RefreshToken newRefreshToken = createRefreshToken(user);

        return LoginResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .role(user.getRole().toString())
                .build();
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();
        return repository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now()) || token.isRevoked()) {
            repository.delete(token);
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired.");
        }
        return token;
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }
}
