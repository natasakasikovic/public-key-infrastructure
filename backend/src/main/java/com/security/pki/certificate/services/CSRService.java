package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.CertificateType;
import com.security.pki.certificate.dtos.CertificateRequestDto;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CSRService {

    private final CertificateService certificateService;
    private final AuthService authService;

    public void processCsrUpload(String caId, String until, MultipartFile csrFile) {
        try {
            PKCS10CertificationRequest csr = loadCsr(csrFile);
            User user = authService.getCurrentUser();
            PublicKey publicKey = extractKey(csr);

            CertificateRequestDto request = mapToDto(csr, user, caId, until);

            // invoke certificate creation
            // certificateService.createEndEntityCertificate(request, publicKey);
            System.out.println("Certificate request: " + request);
        } catch (Exception ex) {
            throw new RuntimeException("CSR processing failed: " + ex.getMessage(), ex);
        }
    }

    private PKCS10CertificationRequest loadCsr(MultipartFile file) throws Exception {
        try (PEMParser parser = new PEMParser(new InputStreamReader(file.getInputStream()))) {
            Object parsed = parser.readObject();
            if (parsed instanceof PKCS10CertificationRequest csr) {
                return csr;
            }
            throw new IllegalArgumentException("Provided file is not a valid CSR");
        }
    }

    private PublicKey extractKey(PKCS10CertificationRequest csr) throws Exception {
        return new JcaPEMKeyConverter().getPublicKey(csr.getSubjectPublicKeyInfo());
    }

    private CertificateRequestDto mapToDto(PKCS10CertificationRequest csr,
                                           User user,
                                           String caId,
                                           String until) {

        X500Name x500 = csr.getSubject();

        return CertificateRequestDto.builder()
                .commonName(readAttr(x500, BCStyle.CN).orElse("unknown"))
                .surname(readAttr(x500, BCStyle.SURNAME).orElse(user.getLastName()))
                .givenName(readAttr(x500, BCStyle.GIVENNAME).orElse(user.getFirstName()))
                .organization(readAttr(x500, BCStyle.O).orElse(user.getOrganization()))
                .organizationalUnit(readAttr(x500, BCStyle.OU).orElse(null))
                .country(readAttr(x500, BCStyle.C).orElse(null))
                .email(readAttr(x500, BCStyle.E).orElse(null))
                .userId(user.getId())
                .validFrom(LocalDate.now())
                .validTo(LocalDate.parse(until))
                .caCertificateId(UUID.fromString(caId))
                .certificateType(CertificateType.END_ENTITY)
                .build();
    }

    private Optional<String> readAttr(X500Name name, ASN1ObjectIdentifier field) {
        RDN[] entries = name.getRDNs(field);
        if (entries.length > 0 && entries[0].getFirst() != null) {
            return Optional.of(entries[0].getFirst().getValue().toString());
        }
        return Optional.empty();
    }
}
