package com.security.pki.certificate.validators.certificate;

import com.security.pki.certificate.exceptions.CertificateValidatorException;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.certificate.models.Certificate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

@Component
@RequiredArgsConstructor
public class CertificateDigitalSignatureValidator implements CertificateValidator {

    private final CertificateMapper mapper;

    @Override
    public void validate(CertificateValidationContext context) {
        Certificate signingCertificate = context.signingCertificate();
        KeyPair signingCertificateKeyPair = context.signingCertificateKeyPair();
        KeyPair signingCertificateParentKeyPair = context.signingCertificateParentKeyPair();

        X509Certificate signingX509Certificate = mapper.toX509(signingCertificate);
        PublicKey verificationKey;

        if (signingCertificate.getParent() == null) // Determine the public key to use for signature verification
            verificationKey = signingCertificateKeyPair.getPublic(); // The signing certificate is a self-signed (ROOT) certificate
        else
            verificationKey = signingCertificateParentKeyPair.getPublic();  // The signing certificate was issued by a parent CA

        try {
            signingX509Certificate.verify(verificationKey);
        } catch (Exception e) {
            throw new CertificateValidatorException("Digital signature of signing certificate is invalid.");
        }
    }
}