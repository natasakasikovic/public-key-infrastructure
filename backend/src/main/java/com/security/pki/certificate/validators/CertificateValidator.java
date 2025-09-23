package com.security.pki.certificate.validators;

public interface CertificateValidator {
    void validate(CertificateValidationContext context);
}