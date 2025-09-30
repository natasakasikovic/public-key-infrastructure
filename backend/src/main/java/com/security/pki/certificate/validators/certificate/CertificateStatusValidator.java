package com.security.pki.certificate.validators.certificate;

import com.security.pki.certificate.exceptions.CertificateValidatorException;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.enums.Status;
import org.springframework.stereotype.Component;

@Component
public class CertificateStatusValidator implements CertificateValidator {

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate certificate = context.signingCertificate();
        if (certificate.getStatus() == Status.REVOKED)
            throw new CertificateValidatorException(String.format("Certificate with serial number %s is revoked.", certificate.getSerialNumber()));
    }
}