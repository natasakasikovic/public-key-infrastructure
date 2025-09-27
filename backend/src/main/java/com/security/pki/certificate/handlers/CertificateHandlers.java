package com.security.pki.certificate.handlers;

import com.security.pki.certificate.exceptions.CertificateNotAllowedToSignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateHandlers {
    @ExceptionHandler(CertificateNotAllowedToSignException.class)
    public ResponseEntity<String> handleCertificateNotAllowedToSignException(CertificateNotAllowedToSignException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
}
