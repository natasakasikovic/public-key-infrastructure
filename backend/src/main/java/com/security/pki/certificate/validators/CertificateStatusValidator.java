package com.security.pki.certificate.validators;

import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Status;

public class CertificateStatusValidator {
    public void validate(Certificate certificate) {
        if (certificate.getStatus() == Status.REVOKED)
            throw new IllegalStateException(String.format("Certificate with serial number %s is revoked.", certificate.getSerialNumber()));
    }
}