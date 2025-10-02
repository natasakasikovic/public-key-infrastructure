package com.security.pki.certificate.validators.certificate;

import com.security.pki.certificate.exceptions.CertificateValidatorException;
import com.security.pki.certificate.models.Certificate;
import org.springframework.stereotype.Component;

@Component
public class PathLenConstraintValidator implements CertificateValidator {

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate parent = context.signingCertificate();
        Certificate child = context.childCertificate();

        Integer parentPathLen = parent.getPathLenConstraint();

        if (parentPathLen == null)
            return;

        if (parentPathLen == 0 && child.isCanSign())
            throw new CertificateValidatorException("Signing certificate has pathLenConstraint=0 and cannot issue CA certificates.");
    }
}