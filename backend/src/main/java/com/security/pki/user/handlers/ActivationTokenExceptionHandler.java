package com.security.pki.user.handlers;

import com.security.pki.shared.models.ExceptionResponse;
import com.security.pki.user.exceptions.ActivationTokenAlreadyUsedException;
import com.security.pki.user.exceptions.ActivationTokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ActivationTokenExceptionHandler {
    @ExceptionHandler(ActivationTokenExpiredException.class)
    public ResponseEntity<ExceptionResponse> handleActivationTokenExpiredHandler(ActivationTokenExpiredException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ActivationTokenAlreadyUsedException.class)
    public ResponseEntity<ExceptionResponse> handleActivationTokenAlreadyUsedHandler(ActivationTokenAlreadyUsedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.CONFLICT.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
