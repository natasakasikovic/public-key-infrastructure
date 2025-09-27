package com.security.pki.certificate.validators;

import com.security.pki.certificate.exceptions.CertificateNotAllowedToSignException;
import com.security.pki.certificate.models.Certificate;
import org.springframework.stereotype.Component;

@Component
public class CertificateSigningPermissionValidator implements CertificateValidator {

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate signingCertificate = context.signingCertificate();
        if (signingCertificate != null && !signingCertificate.isCanSign())
            throw new CertificateNotAllowedToSignException(
                    String.format("Certificate with serial number %s is not allowed to sign other certificates.", signingCertificate.getSerialNumber()));
    }
}