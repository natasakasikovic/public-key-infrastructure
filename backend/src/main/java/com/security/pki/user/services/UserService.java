package com.security.pki.user.services;

import com.security.pki.security.utils.JwtUtil;
import com.security.pki.user.dtos.LoginRequestDto;
import com.security.pki.user.dtos.LoginResponseDto;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final UserMapper mapper;
    private final JwtUtil jwtUtil;

    public void register(RegistrationRequestDto request) {
        User user = mapper.fromRequest(request);
        repository.save(user);
        // TODO: generate a VerificationToken and send an email
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authentication = authenticateUser(request.getEmail(), request.getPassword());
        User user = (User) authentication.getPrincipal();
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
