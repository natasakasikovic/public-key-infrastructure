package com.security.pki.certificate.handlers;

import com.security.pki.certificate.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateHandlers {

    private static final Logger logger = LoggerFactory.getLogger(CertificateHandlers.class);

    @ExceptionHandler(CertificateNotAllowedToSignException.class)
    public ResponseEntity<String> handleCertificateNotAllowedToSignException(CertificateNotAllowedToSignException ex) {
        logger.warn("Not allowed to sign certificate: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    @ExceptionHandler({KeyGenerationException.class, CertificateGenerationException.class, CertificateDownloadException.class})
    public ResponseEntity<String> handleInternalServerErrors(RuntimeException ex) {
        logger.error("Internal server error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(RevocationException.class)
    public ResponseEntity<String> handleRevocationException(RevocationException ex) {
        logger.error("Revocation exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CrlUpdateException.class)
    public ResponseEntity<String> handleCrlUpdateException(CrlUpdateException ex) {
        logger.error("Crl update exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
