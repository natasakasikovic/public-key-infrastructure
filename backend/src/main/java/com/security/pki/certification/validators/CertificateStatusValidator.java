package com.security.pki.certification.validators;

import com.security.pki.certification.models.Certificate;
import com.security.pki.certification.models.Status;

public class CertificateStatusValidator {
    public void validate(Certificate certificate) {
        if (certificate.getStatus() == Status.REVOKED)
            throw new IllegalStateException(String.format("Certificate with serial number %s is revoked.", certificate.getSerialNumber()));
    }
}