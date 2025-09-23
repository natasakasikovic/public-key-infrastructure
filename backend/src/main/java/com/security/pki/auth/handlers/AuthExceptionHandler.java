package com.security.pki.auth.handlers;

import com.security.pki.auth.exceptions.AccountNotVerifiedException;
import com.security.pki.auth.exceptions.InvalidRefreshTokenException;
import com.security.pki.shared.models.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ExceptionResponse> handleAccountNotVerifiedException(AccountNotVerifiedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ExceptionResponse> handleAuthenticationExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                        .message("Invalid email or password.")
                        .build());
    }
}
