package com.security.pki;

import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.Issuer;
import com.security.pki.certificate.models.Status;
import com.security.pki.certificate.models.Subject;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.services.CertificateService;
import com.security.pki.certificate.services.CryptoService;
import com.security.pki.certificate.utils.CertificateGenerator;
import com.security.pki.certificate.utils.CertificateUtils;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import com.security.pki.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestCertificateSeeder implements CommandLineRunner {

    private final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CertificateGenerator certificateGenerator;
    private final CryptoService cryptoService;

    @Override
    public void run(String... args) throws Exception {
        User owner1 = userRepository.findByEmail("admin@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        createTestCertificate("Test Root 1", "OrgA", owner1);
        createTestCertificate("Test Root 2", "OrgB", owner1);
    }

    private void createTestCertificate(String cn, String org, User owner) throws GeneralSecurityException, OperatorCreationException, CertIOException {
        KeyPair keyPair = cryptoService.generateKeyPair();

        X500Name x500Name = new X500NameBuilder(BCStyle.INSTANCE)
                .addRDN(BCStyle.CN, cn)
                .addRDN(BCStyle.O, org)
                .addRDN(BCStyle.OU, "Unit")
                .addRDN(BCStyle.C, "RS")
                .addRDN(BCStyle.ST, "Novi Sad")
                .addRDN(BCStyle.L, "Serbia")
                .build();

        BigInteger serialNumber = CertificateUtils.generateSerialNumber();

        CreateRootCertificateRequest request = CreateRootCertificateRequest.builder()
                .commonName(cn)
                .organization(org)
                .organizationalUnit("Unit")
                .country("RS")
                .state("Novi Sad")
                .locality("Serbia")
                .validFrom(new Date())
                .validTo(Date.from(LocalDate.now().plusYears(2)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .keyUsages(List.of("Digital Signature", "Key Encipherment"))
                .extendedKeyUsages(List.of("TSL Web Server Authentication"))
                .build();

        X509Certificate x509Certificate = certificateGenerator.generateRootCertificate(request, keyPair, serialNumber, x500Name);

        SecretKey dek = cryptoService.generateDek();
        byte[] encryptedPrivateKey = cryptoService.encrypt(dek, keyPair.getPrivate().getEncoded());
        SecretKey wrappingKey = cryptoService.loadMasterKey();
        byte[] wrappedDek = cryptoService.wrapDek(wrappingKey, dek);

        Certificate certificate = Certificate.builder()
                .id(UUID.randomUUID())
                .serialNumber(serialNumber.toString())
                .subject(new Subject(x500Name))
                .issuer(new Issuer(x500Name))
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .status(Status.ACTIVE)
                .canSign(true)
                .certificateData(x509Certificate.getEncoded())
                .encryptedPrivateKey(encryptedPrivateKey)
                .wrappedDek(wrappedDek)
                .owner(owner)
                .build();

        certificateRepository.save(certificate);
    }
}



