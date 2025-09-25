package com.security.pki.auth.services;

import com.security.pki.auth.models.RefreshToken;
import com.security.pki.security.utils.JwtUtil;
import com.security.pki.shared.services.LoggerService;
import com.security.pki.shared.utils.ClientUtils;
import com.security.pki.shared.utils.LogFormat;
import com.security.pki.auth.dtos.LoginRequestDto;
import com.security.pki.auth.dtos.LoginResponseDto;
import com.security.pki.auth.exceptions.AccountNotVerifiedException;
import com.security.pki.user.enums.Role;
import com.security.pki.user.models.User;
import com.security.pki.user.services.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoggerService loggerService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    private final RefreshTokenService refreshTokenService;

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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        loggerService.info(LogFormat.loginSuccess(email, clientIp, user.getId()));
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .role(user.getRole().toString())
                .build();
    }

    public Role getCurrentUserRole() {
        User user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        return authenticationManager.authenticate(token);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails details) {
                return userService.findByEmail(details.getUsername());
            }
        }
        return null;
    }
}
