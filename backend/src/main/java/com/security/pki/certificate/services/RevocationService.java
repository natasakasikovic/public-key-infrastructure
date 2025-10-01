package com.security.pki.certificate.services;

import com.security.pki.certificate.dtos.RevocationRequestDto;
import com.security.pki.certificate.dtos.RevocationResponseDto;
import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.exceptions.CrlUpdateException;
import com.security.pki.certificate.exceptions.RevocationException;
import com.security.pki.certificate.mappers.CertificateRevocationMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.CertificateRevocation;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.repositories.CertificateRevocationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevocationService {

    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;
    private final CertificateRevocationRepository repository;
    private final CertificateRevocationMapper mapper;

    public RevocationResponseDto revoke(UUID certificateId, RevocationRequestDto request) {
        Certificate certificate = certificateRepository.findById(certificateId).orElse(null);

        if (certificate == null)
            throw new EntityNotFoundException("Certificate not found.");
        if (certificate.getStatus() == Status.REVOKED)
            throw new RevocationException("Certificate already revoked.");

        certificate.setStatus(Status.REVOKED);
        certificateRepository.save(certificate);
        CertificateRevocation revocation = repository.save(mapper.toCertificateRevocation(request, certificate));

        updateCrl(certificate);
        return mapper.toCertificateResponseDto(revocation);
    }

    private void updateCrl(Certificate revokedCertificate) {
        try {
            Certificate caCertEntity = revokedCertificate.getParent() != null
                    ? revokedCertificate.getParent()
                    : revokedCertificate;

            X509Certificate caCert = (X509Certificate) CertificateFactory
                    .getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(caCertEntity.getCertificateData()));

            KeyPair keyPair = certificateService.loadKeyPair(caCertEntity);
            PrivateKey caPrivateKey = keyPair.getPrivate();

            X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());
            Date now = new Date();
            Date nextUpdate = Date.from(now.toInstant().plus(1, ChronoUnit.DAYS));

            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuer, now);
            crlBuilder.setNextUpdate(nextUpdate);

            List<CertificateRevocation> revocations =
                    repository.findAllByCertificate_Owner_Id(revokedCertificate.getOwner().getId());
            for (CertificateRevocation rev : revocations) {
                BigInteger serial = new BigInteger(rev.getCertificate().getSerialNumber(), 16);
                Date revokedAt = Date.from(
                        rev.getRevocationDate().atZone(ZoneId.systemDefault()).toInstant()
                );

                ExtensionsGenerator extGen = new ExtensionsGenerator();
                extGen.addExtension(
                        Extension.reasonCode,
                        false,
                        CRLReason.lookup(rev.getReason().getCode())
                );

                crlBuilder.addCRLEntry(serial, revokedAt, extGen.generate());
            }

            BigInteger crlNumber = BigInteger.valueOf(System.currentTimeMillis());
            crlBuilder.addExtension(Extension.cRLNumber, false, new CRLNumber(crlNumber));

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                    .setProvider("BC")
                    .build(caPrivateKey);

            X509CRLHolder crlHolder = crlBuilder.build(signer);
            JcaX509CRLConverter converter = new JcaX509CRLConverter().setProvider("BC");
            X509CRL crl = converter.getCRL(crlHolder);

            Path crlPath = Paths.get("crl", caCertEntity.getSerialNumber() + ".crl");
            Files.createDirectories(crlPath.getParent());
            Files.write(crlPath, crl.getEncoded());

        } catch (Exception e) {
            throw new CrlUpdateException("Failed to update CRL");
        }
    }

    @Transactional
    public Resource getCrl(String serialNumber) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber).orElse(null);

        if (certificate == null)
            throw new EntityNotFoundException("Certificate not found.");

        Resource resource = new FileSystemResource(
                Paths.get("crl", serialNumber + ".crl")
        );

        if (!resource.exists())
            throw new EntityNotFoundException("Certificate revocation list not found");

        return resource;
    }
}
