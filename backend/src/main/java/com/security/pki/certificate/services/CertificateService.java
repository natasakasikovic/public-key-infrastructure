package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CertificateDetailsResponseDto;
import com.security.pki.certificate.dtos.CertificateResponseDto;
import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.exceptions.CertificateCreationException;
import com.security.pki.certificate.exceptions.CertificateDownloadException;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.utils.CertificateGenerator;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.certificate.utils.KeyStoreService;
import com.security.pki.certificate.validators.CertificateValidationContext;
import com.security.pki.certificate.validators.CertificateValidator;
import com.security.pki.shared.models.PagedResponse;
import com.security.pki.shared.services.LoggerService;
import com.security.pki.user.enums.Role;
import com.security.pki.user.models.User;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;

    private final AuthService authService;
    private final CryptoService cryptoService;
    private final CertificateGenerator certificateGenerator;
    private final KeyStoreService keyStoreService;
    private final LoggerService loggerService;

    private final CertificateMapper mapper;
    private final List<CertificateValidator> validators;

    @Transactional
    public void createRootCertificate(CreateRootCertificateRequest request) {
        try {
            final KeyPair keyPair = cryptoService.generateKeyPair();
            final X500Name x500Name = buildX500Name(request);
            final BigInteger serialNumber = CertificateUtils.generateSerialNumber();
            final Certificate certificate = buildCertificateEntity(request, x500Name, serialNumber);
            certificate.setOwner(authService.getCurrentUser());
            final X509Certificate x509Certificate = certificateGenerator.generateRootCertificate(request, keyPair, serialNumber, x500Name);
            storeCertificate(certificate, x509Certificate, keyPair.getPrivate());
        } catch (Exception e) {
            loggerService.warning("Failed to generate certificate: " + e.getMessage());
            throw new CertificateCreationException("An error occurred while generating the certificate. Please try again later.");
        }
    }

    @Transactional
    protected void storeCertificate(Certificate certificate, X509Certificate x509Certificate, PrivateKey privateKey) throws GeneralSecurityException {
        SecretKey dek = cryptoService.generateDek();
        byte[] encryptedPrivateKey = cryptoService.encrypt(dek, privateKey.getEncoded());

        SecretKey wrappingKey = cryptoService.loadMasterKey();
        byte[] wrappedDek = cryptoService.wrapDek(wrappingKey, dek);

        certificate.setEncryptedPrivateKey(encryptedPrivateKey);
        certificate.setWrappedDek(wrappedDek);
        certificate.setCertificateData(x509Certificate.getEncoded());

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

        // TODO: think about removing method in AuthService (getCurrentUserRole) if you don't use it
        String signingSerialNumber = request.getSigningSerialNumber();
        Certificate signingCertificate = null;

        if (signingSerialNumber != null)
            signingCertificate =findBySerialNumber(signingSerialNumber);

        CertificateValidationContext context = new CertificateValidationContext(signingCertificate, mapper.fromRequest(request));

        for (CertificateValidator validator : validators)
            validator.validate(context);

        Certificate certificate = Certificate.builder().build(); // TODO: fill with params
        // TODO: save certificate
    }

    @Transactional
    public Resource exportAsPkcs12(String serialNumber) {
        Certificate certificate = findBySerialNumber(serialNumber);
        try {
            X509Certificate cert = mapper.toX509(certificate);

            SecretKey masterKey = cryptoService.loadMasterKey();
            SecretKey dek = cryptoService.unwrapDek(masterKey, certificate.getWrappedDek());

            byte[] privateKeyBytes = cryptoService.decrypt(dek, certificate.getEncryptedPrivateKey());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            return keyStoreService.generatePkcs12Resource("alias", privateKey, "changeit".toCharArray(), new java.security.cert.Certificate[]{cert});

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

        // BFS
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

        List<Certificate> all = new ArrayList<>(collected.values());
        all.sort(Comparator.comparing(Certificate::getValidTo, Comparator.nullsLast(Comparator.reverseOrder())));
        return mapper.toPagedResponse(toPage(all, pageable));
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