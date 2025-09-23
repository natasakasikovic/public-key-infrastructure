package com.security.pki.certification.validators;

import com.security.pki.certification.models.Certificate;

public class CertificateValidityPeriodValidator{

    public void validate(Certificate childCertificate, Certificate parentCertificate) {

        if (parentCertificate == null) return;

        if (childCertificate.getValidFrom().before(parentCertificate.getValidTo()))
            throw new IllegalArgumentException("NotBefore cannot be earlier than the signing certificate's NotBefore!");

        if (childCertificate.getValidFrom().after(parentCertificate.getValidTo()))
            throw new IllegalArgumentException("NotAfter cannot be later than the signing certificate's NotAfter!");
    }
}