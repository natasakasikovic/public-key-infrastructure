package com.security.pki.certificate.handlers;

import com.security.pki.certificate.exceptions.CertificateCreationException;
import com.security.pki.certificate.exceptions.CertificateDownloadException;
import com.security.pki.certificate.exceptions.CertificateNotAllowedToSignException;
import com.security.pki.certificate.exceptions.RootCertificateIssuanceNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateHandlers {

    @ExceptionHandler(RootCertificateIssuanceNotAllowedException.class)
    public ResponseEntity<String> handleRootCertificateIssuanceNotAllowed(RootCertificateIssuanceNotAllowedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CertificateNotAllowedToSignException.class)
    public ResponseEntity<String> handleCertificateNotAllowedToSignException(CertificateNotAllowedToSignException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ CertificateCreationException.class, CertificateDownloadException.class })
    public ResponseEntity<String> handleCertificateNotAllowedToSignException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
