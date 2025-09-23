package com.security.pki.certificate.validators;

import com.security.pki.certificate.models.Certificate;

public class CertificateValidityPeriodValidator{

    public void validate(Certificate childCertificate, Certificate parentCertificate) {

        if (parentCertificate == null) return;

        if (childCertificate.getValidFrom().before(parentCertificate.getValidFrom()))
            throw new IllegalArgumentException("The certificate's valid-from date cannot be earlier than the signing certificate's valid-from date.");

        if (childCertificate.getValidTo().after(parentCertificate.getValidTo()))
            throw new IllegalArgumentException("The certificate's valid-to date cannot be later than the signing certificate's valid-to date.");

        if (childCertificate.getValidFrom().after(childCertificate.getValidTo()))
            throw new IllegalArgumentException("The certificate's valid-from date cannot be after its valid-to date.");
    }
}