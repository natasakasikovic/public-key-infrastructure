package com.security.pki.auth.services;

import com.security.pki.security.utils.JwtUtil;
import com.security.pki.shared.services.LoggerService;
import com.security.pki.shared.utils.ClientUtils;
import com.security.pki.shared.utils.LogFormat;
import com.security.pki.auth.dtos.LoginRequestDto;
import com.security.pki.auth.dtos.LoginResponseDto;
import com.security.pki.auth.exceptions.AccountNotVerifiedException;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoggerService loggerService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto login(LoginRequestDto request) {
        String email = request.getEmail();
        String clientIp = ClientUtils.getClientIp();

        loggerService.info(LogFormat.loginAttempt(email, clientIp));
        Authentication authentication = authenticateUser(request.getEmail(), request.getPassword());
        User user = (User) authentication.getPrincipal();

        if(Boolean.FALSE.equals(user.getVerified())) {
            loggerService.warning(LogFormat.loginFailure(email, clientIp, "AccountNotVerified"));
            throw new AccountNotVerifiedException("Your account is not verified. Please check your email.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().toString(), user.getId());
        // TODO: Think about adding RefreshToken to HttpOnly cookie
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

        loggerService.info(LogFormat.loginSuccess(email, clientIp, user.getId()));
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
