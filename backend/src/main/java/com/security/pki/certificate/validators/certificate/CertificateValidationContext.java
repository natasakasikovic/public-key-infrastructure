package com.security.pki.certificate.validators.certificate;

import com.security.pki.certificate.models.Certificate;

import java.security.KeyPair;

public record CertificateValidationContext(Certificate signingCertificate, Certificate childCertificate, KeyPair signingCertificateKeyPair, KeyPair signingCertificateParentKeyPair) {}