package com.security.pki.user.services;

import com.security.pki.security.utils.JwtUtil;
import com.security.pki.shared.services.EmailService;
import com.security.pki.user.dtos.LoginRequestDto;
import com.security.pki.user.dtos.LoginResponseDto;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.exceptions.AccountNotVerifiedException;
import com.security.pki.user.exceptions.ActivationTokenAlreadyUsedException;
import com.security.pki.user.exceptions.ActivationTokenExpiredException;
import com.security.pki.user.exceptions.EmailAlreadyTakenException;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.ActivationToken;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResponseDto register(RegistrationRequestDto request) {
        if (Boolean.TRUE.equals(repository.existsByEmail(request.getEmail())))
            throw new EmailAlreadyTakenException("This email is already taken. Please log in or activate your account via the email we sent you.");

        User user = mapper.fromRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticateUser(request.getEmail(), request.getPassword());
        User user = (User) authentication.getPrincipal();

        if(Boolean.FALSE.equals(user.getVerified()))
            throw new AccountNotVerifiedException("Your account is not verified. Please check your email.");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().toString(), user.getId());
        // TODO: Think about adding RefreshToken to HttpOnly cookie
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().toString())
                .build();
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(token);
    }

}
