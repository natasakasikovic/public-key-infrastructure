package com.security.pki.certificate.validators.certificate;

import com.security.pki.certificate.exceptions.CertificateValidatorException;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.SAN;
import org.springframework.stereotype.Component;

@Component
public class CertificateTemplateValidator implements CertificateValidator {

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate child = context.childCertificate();
        validateTtl(context, child);
        validateCommonName(context, child);
        validateSAN(context, child);
    }

    private void validateTtl(CertificateValidationContext context, Certificate child) {
        if (context.ttlDays() != null && child.getValidFrom() != null && child.getValidTo() != null) {
            long diffMillis = child.getValidTo().getTime() - child.getValidFrom().getTime();
            long diffDays = diffMillis / (1000 * 60 * 60 * 24);

            if (diffDays > context.ttlDays()) {
                throw new CertificateValidatorException(
                        "Certificate validity period (" + diffDays + " days) exceeds allowed TTL of " + context.ttlDays() + " days."
                );
            }
        }
    }

    private void validateCommonName(CertificateValidationContext context, Certificate child) {
        if (context.commonNameRegex() != null && child.getSubject() != null) {
            String cn = child.getSubject().getPrincipalName();
            if (cn == null || !cn.matches(context.commonNameRegex())) {
                throw new CertificateValidatorException(
                        "Common Name (CN) '" + cn + "' does not match required pattern: " + context.commonNameRegex()
                );
            }
        }
    }

    private void validateSAN(CertificateValidationContext context, Certificate child) {
        if (context.sanRegex() != null && child.getSubjectAlternativeNames() != null) {
            for (SAN san : child.getSubjectAlternativeNames()) {
                if (!san.getValue().matches(context.sanRegex())) {
                    throw new CertificateValidatorException(
                            "SAN value '" + san.getValue() + "' does not match required pattern: " + context.sanRegex()
                    );
                }
            }
        }
    }
}