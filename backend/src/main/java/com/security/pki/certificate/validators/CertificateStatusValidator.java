package com.security.pki.certificate.validators;

import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Status;
import org.springframework.stereotype.Component;

@Component
public class CertificateStatusValidator implements CertificateValidator{

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate certificate = context.signingCertificate();
        if (certificate.getStatus() == Status.REVOKED)
            throw new IllegalStateException(String.format("Certificate with serial number %s is revoked.", certificate.getSerialNumber()));
    }
}