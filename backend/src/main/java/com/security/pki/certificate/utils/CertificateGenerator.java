package com.security.pki.certificate.utils;

import com.security.pki.certificate.converters.ExtendedKeyUsageConverter;
import com.security.pki.certificate.converters.KeyUsageConverter;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.dtos.CreateSubordinateCertificateDto;
import com.security.pki.certificate.exceptions.CertificateGenerationException;
import com.security.pki.certificate.models.Certificate;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CertificateGenerator {

    @Value("${app.base.url}")
    private String baseUrl;

    public X509Certificate generateRootCertificate(CreateRootCertificateRequest request, KeyPair keyPair, BigInteger serialNumber, X500Name x500Name) {
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

            // key usages
            certBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsageConverter.convertKeyUsageToInt(request.getKeyUsages(), true)));

            // extended key usages
            certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(ExtendedKeyUsageConverter.convertToExtendedKeyUsages(request.getExtendedKeyUsages())));

            // basic constraints
            certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

            // subject key identifier
            SubjectKeyIdentifier ski = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
            certBuilder.addExtension(Extension.subjectKeyIdentifier, false, ski);

            // CRLDistPoint
            CRLDistPoint distPoint = buildCRLDistPoint(serialNumber);
            certBuilder.addExtension(Extension.cRLDistributionPoints, false, distPoint);

            final X509CertificateHolder certHolder = certBuilder.build(contentSigner);
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        } catch (OperatorCreationException e) {
            throw new CertificateGenerationException(String.format("Error creating signature operator: %s", e.getMessage()));
        } catch (CertIOException e) {
            throw new CertificateGenerationException(String.format("Error processing certificate extensions: %s", e.getMessage()));
        } catch (CertificateException e) {
            throw new CertificateGenerationException(String.format("Error converting certificate object: %s", e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
        throw new CertificateGenerationException(String.format("Required cryptographic algorithm not found: %s", e.getMessage()));
        }
    }

    public X509Certificate generateSubordinateCertificate(CreateSubordinateCertificateDto request, Certificate signingCertificate, KeyPair keyPair, X500Name subject, BigInteger serialNumber, PublicKey parentPublicKey) {
        try {
            final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                    .setProvider("BC").build(keyPair.getPrivate());

            X500Name issuer = signingCertificate.getSubject().toX500Name();

            final JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer,
                    serialNumber,
                    request.getValidFrom(),
                    request.getValidTo(),
                    subject,
                    keyPair.getPublic()
            );

            // subject key identifier
            SubjectKeyIdentifier subjectKeyIdentifier = new JcaX509ExtensionUtils().createSubjectKeyIdentifier(keyPair.getPublic());
            certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier);

            // authority key identifier
            AuthorityKeyIdentifier aki = new JcaX509ExtensionUtils().createAuthorityKeyIdentifier(parentPublicKey);
            certBuilder.addExtension(Extension.authorityKeyIdentifier, false, aki);

            // basic constraints
            if (request.getCanSign()) // if it is intermediate, set basic constraints to true, otherwise false (end-entity)
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            else
                certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

            // key usage
            if (request.getKeyUsages() != null && !request.getKeyUsages().isEmpty())
                certBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsageConverter.convertKeyUsageToInt(request.getKeyUsages(), request.getCanSign())));

            // extended key usage
            if (request.getExtendedKeyUsages() != null && !request.getExtendedKeyUsages().isEmpty())
                certBuilder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(ExtendedKeyUsageConverter.convertToExtendedKeyUsages(request.getExtendedKeyUsages())));

            // subject alternative names
            List<GeneralName> sanList = new ArrayList<>();
            sanList.add(new GeneralName(GeneralName.dNSName, "localhost"));
            sanList.add(new GeneralName(GeneralName.iPAddress, "127.0.0.1"));
            sanList.add(new GeneralName(GeneralName.iPAddress, "::1"));

            GeneralNames subjectAltNames = new GeneralNames(sanList.toArray(new GeneralName[0]));
            certBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltNames);

            // CRLDistPoint
            CRLDistPoint distPoint = buildCRLDistPoint(serialNumber);
            certBuilder.addExtension(Extension.cRLDistributionPoints, false, distPoint);

            final X509CertificateHolder certHolder = certBuilder.build(contentSigner);
            return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        } catch (OperatorCreationException e) {
            throw new CertificateGenerationException(String.format("Error creating signature operator: %s", e.getMessage()));
        } catch (CertIOException e) {
            throw new CertificateGenerationException(String.format("Error processing certificate extensions: %s", e.getMessage()));
        } catch (CertificateException e) {
            throw new CertificateGenerationException(String.format("Error converting certificate object: %s", e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateGenerationException(String.format("Required cryptographic algorithm not found: %s", e.getMessage()));
        }
    }

    private CRLDistPoint buildCRLDistPoint(BigInteger serialNumber) {
        String crlUrl = String.format("%s/%d/crl", baseUrl, serialNumber);

        return new CRLDistPoint(new DistributionPoint[]{
                new DistributionPoint(
                        null,
                        null,
                        new GeneralNames(new GeneralName(GeneralName.uniformResourceIdentifier, crlUrl))
                )
        });
    }
}