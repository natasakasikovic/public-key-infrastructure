package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.models.Status;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.utils.CertificateGenerator;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.certificate.utils.KeyStoreService;
import com.security.pki.certificate.validators.CertificateValidationContext;
import com.security.pki.certificate.validators.CertificateValidator;
import com.security.pki.user.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final KeyStoreService keyStoreService;
    private final CertificateGenerator certificateGenerator;

    private final CertificateMapper mapper;
    private final List<CertificateValidator> validators;


    public void createRootCertificate(CreateRootCertificateRequest request) throws NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, CertificateException, CertIOException {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        final X500Name x500Name = buildX500Name(request);
        final BigInteger serialNumber = CertificateUtils.generateSerialNumber();
        final Certificate certificate = buildCertificateEntity(request, x500Name, serialNumber);
        final X509Certificate x509Certificate = certificateGenerator.generateRootCertificate(request, keyPair, serialNumber, x500Name);
        final String password = CertificateUtils.generatePassword(20);

        keyStoreService.loadKeyStore("keystore/test", password.toCharArray());
        keyStoreService.write(
                serialNumber.toString(),
                keyPair.getPrivate(),
                password.toCharArray(),
                new java.security.cert.Certificate[]{x509Certificate}
        );
        keyStoreService.saveKeyStore(serialNumber + ".p12", password.toCharArray());

        repository.save(certificate);
    }

    private X500Name buildX500Name(CreateRootCertificateRequest request) {
        final X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);

        builder.addRDN(BCStyle.CN, request.getCommonName());
        builder.addRDN(BCStyle.O, request.getOrganization());
        builder.addRDN(BCStyle.OU, request.getOrganizationalUnit());
        builder.addRDN(BCStyle.C, request.getCountry());
        builder.addRDN(BCStyle.ST, request.getState());
        builder.addRDN(BCStyle.L, request.getLocality());

        return builder.build();
    }

    private Certificate buildCertificateEntity(CreateRootCertificateRequest request, X500Name x500Name, BigInteger serialNumber) {
        return Certificate.builder()
                .id(UUID.randomUUID())
                .serialNumber(serialNumber.toString())
                .subject(new Subject(x500Name))
                .issuer(new Issuer(x500Name))
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .status(Status.ACTIVE)
                .canSign(true)
                .build();
    }

    // NOTE: method below serves for creating intermediate and end-entity certificates (IN PROGRESS)
    public void createCertificate(CreateCertificateDto request) {

        Role sessionUserRole = authService.getCurrentUserRole();
        String signingSerialNumber = request.getSigningSerialNumber();
        Certificate signingCertificate = null;

        if (signingSerialNumber != null)
            signingCertificate =findBySerialNumber(signingSerialNumber);

        CertificateValidationContext context = new CertificateValidationContext(sessionUserRole, signingCertificate, mapper.fromRequest(request));

        for (CertificateValidator validator : validators)
            validator.validate(context);

        Certificate certificate = Certificate.builder().build(); // TODO: fill with params
        // TODO: save certificate
    }

    private Certificate findBySerialNumber(String serialNumber) {
        return repository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Certificate with serial number '%s' was not found.", serialNumber)
                ));
    }
}