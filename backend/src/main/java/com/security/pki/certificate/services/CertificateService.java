package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.*;
import com.security.pki.certificate.exceptions.CertificateDownloadException;
import com.security.pki.certificate.exceptions.CertificateStorageException;
import com.security.pki.certificate.exceptions.KeyPairRetrievalException;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.utils.CertificateGenerator;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.certificate.utils.KeyStoreService;
import com.security.pki.certificate.validators.certificate.CertificateValidationContext;
import com.security.pki.certificate.validators.certificate.CertificateValidator;
import com.security.pki.certificate.validators.certificate.CertificateValidityPeriodValidator;
import com.security.pki.shared.models.PagedResponse;
import com.security.pki.user.enums.Role;
import com.security.pki.user.models.User;
import com.security.pki.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final UserService userService;
    private final CertificateGenerator certificateGenerator;
    private final KeyStoreService keyStoreService;

    private final CertificateValidityPeriodValidator rootValidator;
    private final CertificateMapper mapper;
    private final List<CertificateValidator> validators;

    @Transactional
    public void createRootCertificate(CreateRootCertificateRequest request) {
        final KeyPair keyPair = cryptoService.generateKeyPair();
        final X500Name x500Name = buildX500Name(request);
        final BigInteger serialNumber = CertificateUtils.generateSerialNumber();
        final Certificate certificate = buildCertificateEntity(request, x500Name, serialNumber);
        rootValidator.validate(new CertificateValidationContext(null, certificate));
        certificate.setOwner(authService.getCurrentUser());
        final X509Certificate x509Certificate = certificateGenerator.generateRootCertificate(request, keyPair, serialNumber, x500Name);
        storeCertificate(certificate, x509Certificate, keyPair.getPrivate());
    }

    @Transactional
    protected void storeCertificate(Certificate certificate, X509Certificate x509Certificate, PrivateKey privateKey) {
        try {
            SecretKey dek = cryptoService.generateDek();
            byte[] encryptedPrivateKey = cryptoService.encrypt(dek, privateKey.getEncoded());

            SecretKey wrappingKey = cryptoService.loadMasterKey();
            byte[] wrappedDek = cryptoService.wrapDek(wrappingKey, dek);

            certificate.setEncryptedPrivateKey(encryptedPrivateKey);
            certificate.setWrappedDek(wrappedDek);
            certificate.setCertificateData(x509Certificate.getEncoded());

            repository.save(certificate);

        } catch (GeneralSecurityException e) {
            throw new CertificateStorageException(String.format("Failed to store certificate securely: %s", e.getMessage()));
        }
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
                .pathLenConstraint(null) // for root
                .build();
    }

    @Transactional
    public void createSubordinateCertificate(CreateSubordinateCertificateDto request, PublicKey publicKey) {
        UUID signingCertificateId = request.getSigningCertificateId();
        Certificate signingCertificate = findById(signingCertificateId);
        User user = null;

        user = (request.getUserId() == null) ? authService.getCurrentUser() : userService.findById(request.getUserId());

        X500Name subjectX500Name = buildX500Name(request, user);
        X500Name issuerX500Name = signingCertificate.getSubject().toX500Name();

        final BigInteger serialNumber = CertificateUtils.generateSerialNumber();
        final KeyPair keyPair = cryptoService.generateKeyPair();
        if (publicKey == null)
            publicKey = keyPair.getPublic();
        final KeyPair parentKeyPair = loadKeyPair(signingCertificate); // needed for extensions

        CertificateValidationContext context = new CertificateValidationContext(signingCertificate, mapper.fromRequest(request));

        for (CertificateValidator validator : validators)
            validator.validate(context);

        final X509Certificate x509Certificate = certificateGenerator.generateSubordinateCertificate(request, signingCertificate, publicKey, subjectX500Name, serialNumber, parentKeyPair);
        Certificate certificate = buildCertificateEntity(request, serialNumber, subjectX500Name, issuerX500Name, user, signingCertificate);
        storeCertificate(certificate, x509Certificate, keyPair.getPrivate());
    }

    private X500Name buildX500Name(CreateSubordinateCertificateDto request, User user) {
        final X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);

        builder.addRDN(BCStyle.CN, request.getCommonName());
        builder.addRDN(BCStyle.O, user.getOrganization());
        builder.addRDN(BCStyle.OU, request.getOrganizationalUnit());
        builder.addRDN(BCStyle.C, request.getCountry());
        builder.addRDN(BCStyle.ST, request.getState());
        builder.addRDN(BCStyle.L, request.getLocality());

        return builder.build();
    }

    private Certificate buildCertificateEntity(CreateSubordinateCertificateDto request, BigInteger serialNumber,X500Name subjectX500Name, X500Name issuerX500Name, User user, Certificate signingCertificate) {
        return Certificate.builder()
                .id(UUID.randomUUID())
                .serialNumber(serialNumber.toString())
                .subject(new Subject(subjectX500Name))
                .issuer(new Issuer(issuerX500Name))
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .owner(user)
                .parent(signingCertificate)
                .status(Status.ACTIVE)
                .canSign(request.getCanSign())
                .pathLenConstraint(request.getPathLenConstraint() != null ? request.getPathLenConstraint() : signingCertificate.getPathLenConstraint() - 1)
                .build();
    }

    @Transactional
    public Resource exportAsPkcs12(String serialNumber) {
        Certificate certificate = findBySerialNumber(serialNumber);
        KeyPair keyPair = loadKeyPair(certificate);

        try {
            X509Certificate cert = mapper.toX509(certificate);
            return keyStoreService.generatePkcs12Resource("alias", keyPair.getPrivate(), "changeit".toCharArray(), new java.security.cert.Certificate[]{cert});
        } catch (Exception e) {
            throw new CertificateDownloadException("An error occurred while generating the certificate. Please try again later.");
        }
    }

    public Certificate findBySerialNumber(String serialNumber) {
        return repository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Certificate with serial number '%s' was not found.", serialNumber)
                ));
    }

    public Certificate findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Certificate not found."));
    }

    @Transactional
    public PagedResponse<CertificateResponseDto> getCertificates(Pageable pageable) {
        User user = authService.getCurrentUser();
        Role role = user.getRole();

        return switch (role) {
            case ADMIN -> mapper.toPagedResponse(repository.findAll(pageable));
            case CA_USER -> getCACertificates(user.getId(), pageable);
            case REGULAR_USER -> mapper.toPagedResponse(repository.findByOwner_Id(user.getId(), pageable));
        };
    }

    private PagedResponse<CertificateResponseDto> getCACertificates(Long ownerId, Pageable pageable) {
        List<Certificate> caCerts = repository.findByOwner_IdAndCanSignTrue(ownerId);
        if (caCerts.isEmpty())
            return mapper.toPagedResponse(new PageImpl<>(Collections.emptyList(), pageable, 0));

        LinkedHashMap<String, Certificate> collected = getIssuableCertificateChain(caCerts);

        List<Certificate> all = new ArrayList<>(collected.values());
        all.sort(Comparator.comparing(Certificate::getValidTo, Comparator.nullsLast(Comparator.reverseOrder())));
        return mapper.toPagedResponse(toPage(all, pageable));
    }


    private LinkedHashMap<String, Certificate> getIssuableCertificateChain(List<Certificate> caCerts) {
        LinkedHashMap<String, Certificate> collected = new LinkedHashMap<>();
        Deque<Certificate> stack = new ArrayDeque<>(caCerts);
        for (Certificate ca : caCerts)
            collected.putIfAbsent(ca.getSerialNumber(), ca);

        while (!stack.isEmpty()) {
            Certificate current = stack.pop();
            List<Certificate> children = repository.findByParent_SerialNumber(current.getSerialNumber());
            for (Certificate child : children) {
                if (!collected.containsKey(child.getSerialNumber())) {
                    collected.put(child.getSerialNumber(), child);
                    if (child.isCanSign())
                        stack.push(child);
                }
            }
        }
        return collected;
    }


    public KeyPair loadKeyPair(Certificate certificate) {
        try {
            X509Certificate cert = mapper.toX509(certificate);

            SecretKey masterKey = cryptoService.loadMasterKey();
            SecretKey dek = cryptoService.unwrapDek(masterKey, certificate.getWrappedDek());

            byte[] privateKeyBytes = cryptoService.decrypt(dek, certificate.getEncryptedPrivateKey());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new KeyPairRetrievalException(String.format("Error while loading key pair: %s", e.getMessage()));
        }
    }

    @Transactional
    public PagedResponse<CertificateResponseDto> getValidParentCas(Pageable pageable) {
        Page<Certificate> certificates = repository.findValidParentCas(Status.REVOKED, LocalDateTime.now(), pageable);
        return mapper.toPagedResponse(certificates);
    }

    @Transactional
    public PagedResponse<CertificateResponseDto> getAuthorizedIssuingCertificatesForUser(Pageable pageable) {
        User user = authService.getCurrentUser();
        List<Certificate> certificates = repository.findValidCertificatesByOwner(Status.REVOKED, LocalDateTime.now(), user.getId());
        LinkedHashMap<String, Certificate> issuableChain = getIssuableCertificateChain(certificates);
        List<Certificate> collectedCertificates = new ArrayList<>(issuableChain.values());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), collectedCertificates.size());
        List<Certificate> pageContent = collectedCertificates.subList(start, end);
        Page<Certificate> pagedResult = new PageImpl<>(pageContent, pageable, collectedCertificates.size());

        return mapper.toPagedResponse(pagedResult);
    }

    public CertificateDetailsResponseDto getCertificate(UUID id) {
        return mapper.toDetailsResponse(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Certificate not found.")));
    }

    private Page<Certificate> toPage(List<Certificate> source, Pageable pageable) {
        int total = source.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Certificate> content;
        if (start >= end) {
            content = Collections.emptyList();
        } else {
            content = source.subList(start, end);
        }
        return new PageImpl<>(content, pageable, total);
    }
}