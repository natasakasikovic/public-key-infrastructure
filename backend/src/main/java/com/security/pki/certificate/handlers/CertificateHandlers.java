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

    @ExceptionHandler(CertificateValidatorException.class)
    public ResponseEntity<String> handleCertificateValidatorException(CertificateValidatorException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler({KeyGenerationException.class, CertificateGenerationException.class, CertificateDownloadException.class})
    public ResponseEntity<String> handleInternalServerErrors(RuntimeException ex) {
        logger.error("Internal server error: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(KeyPairRetrievalException.class)
    public ResponseEntity<String> handleKeyPairRetrievalException(KeyPairRetrievalException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}