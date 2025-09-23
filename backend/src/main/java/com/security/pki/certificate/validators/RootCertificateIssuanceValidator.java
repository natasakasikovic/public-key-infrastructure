package com.security.pki.certificate.validators;

import com.security.pki.certificate.exceptions.RootCertificateIssuanceNotAllowedException;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.user.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class RootCertificateIssuanceValidator implements CertificateValidator {

    @Override
    public void validate(CertificateValidationContext context) {
        Role userRole = context.userRole();
        Certificate signingCertificate = context.signingCertificate();
        if (userRole != Role.ADMIN && signingCertificate == null)
            throw new RootCertificateIssuanceNotAllowedException("Only ADMIN users are allowed to issue root certificates.");
    }
}