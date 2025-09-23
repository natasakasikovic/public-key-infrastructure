package com.security.pki.certificate.exceptions;

public class CertificateNotAllowedToSignException extends RuntimeException {
    public CertificateNotAllowedToSignException(String message) {
        super(message);
    }
}