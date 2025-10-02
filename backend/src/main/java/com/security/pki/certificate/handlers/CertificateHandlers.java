package com.security.pki.certificate.handlers;

import com.security.pki.certificate.exceptions.*;
import com.security.pki.shared.models.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CertificateHandlers {
    @ExceptionHandler({
            CertificateValidatorException.class,
            RevocationException.class,
            InvalidCsrException.class,
            CertificateParsingException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler({
            KeyGenerationException.class,
            CertificateGenerationException.class,
            CertificateDownloadException.class,
            CrlException.class,
            KeyPairRetrievalException.class
    })
    public ResponseEntity<ExceptionResponse> handleInternalServerErrors(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.builder()
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .message(ex.getMessage())
                        .build());
    }
}
