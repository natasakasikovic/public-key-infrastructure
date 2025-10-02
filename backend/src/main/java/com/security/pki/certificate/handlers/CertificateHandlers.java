package com.security.pki.certificate.handlers;

import com.security.pki.certificate.exceptions.*;
import com.security.pki.shared.models.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.security.pki.certificate.exceptions.CertificateDownloadException;
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

    @ExceptionHandler(RevocationException.class)
    public ResponseEntity<ExceptionResponse> handleRevocationException(RevocationException ex) {
        logger.error("Revocation exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(CrlException.class)
    public ResponseEntity<ExceptionResponse> handleCrlUpdateException(CrlException ex) {
        logger.error("Crl update exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(KeyPairRetrievalException.class)
    public ResponseEntity<String> handleKeyPairRetrievalException(KeyPairRetrievalException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCsrException.class)
    public ResponseEntity<String> handleInvalidCsr(InvalidCsrException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(CertificateParsingException.class)
    public ResponseEntity<String> handleCertificateParsingException(CertificateParsingException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}