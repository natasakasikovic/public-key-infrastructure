package com.security.pki.certificate.exceptions;

public class RootCertificateIssuanceNotAllowedException extends RuntimeException {
    public RootCertificateIssuanceNotAllowedException(String message) {
        super(message);
    }
}