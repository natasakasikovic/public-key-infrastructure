package com.security.pki.certificate.validators;

import com.security.pki.certificate.models.Certificate;

public record CertificateValidationContext(Certificate signingCertificate, Certificate childCertificate ) {}