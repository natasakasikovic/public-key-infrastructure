package com.security.pki.certificate.validators;

import com.security.pki.certificate.models.Certificate;
import com.security.pki.user.enums.Role;

public record CertificateValidationContext(Role userRole, Certificate signingCertificate, Certificate childCertificate ) {}