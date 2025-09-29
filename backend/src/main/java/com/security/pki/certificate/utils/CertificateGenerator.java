package com.security.pki.certificate.utils;

import com.security.pki.certificate.converters.ExtendedKeyUsageConverter;
import com.security.pki.certificate.converters.KeyUsageConverter;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.exceptions.CertificateGenerationException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Component
public class CertificateGenerator {

    public X509Certificate generateRootCertificate(CreateRootCertificateRequest request, KeyPair keyPair,
                                                   BigInteger serialNumber, X500Name x500Name) {
        try {
            final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .setProvider("BC").build(keyPair.getPrivate());

            final JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    x500Name,
                    serialNumber,
                    request.getValidFrom(),
                    request.getValidTo(),
                    x500Name,
                    keyPair.getPublic()
            );

            certBuilder.addExtension(Extension.keyUsage, false,
                    new KeyUsage(KeyUsageConverter.convertKeyUsageToInt(request.getKeyUsages())));

            certBuilder.addExtension(Extension.extendedKeyUsage, false,
                    new ExtendedKeyUsage(ExtendedKeyUsageConverter.convertToExtendedKeyUsages(request.getExtendedKeyUsages())));

            certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

            final X509CertificateHolder certHolder = certBuilder.build(contentSigner);
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        } catch (OperatorCreationException e) {
            throw new CertificateGenerationException(String.format("Error creating signature operator: %s", e.getMessage()));
        } catch (CertIOException e) {
            throw new CertificateGenerationException(String.format("Error processing certificate extensions: %s", e.getMessage()));
        } catch (CertificateException e) {
            throw new CertificateGenerationException(String.format("Error converting certificate object: %s", e.getMessage()));
        }
    }
}