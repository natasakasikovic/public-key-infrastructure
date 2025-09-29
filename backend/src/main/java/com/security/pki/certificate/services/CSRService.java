package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CertificateRequestDto;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.security.PublicKey;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CSRService {

    private final CertificateService certificateService;
    private final CertificateMapper mapper;
    private final AuthService authService;

    public void processCsrUpload(String caId, String until, MultipartFile csrFile) {
        try {
            PKCS10CertificationRequest csr = loadCsr(csrFile);
            User user = authService.getCurrentUser();
            PublicKey publicKey = extractKey(csr);

            CertificateRequestDto request = mapper.fromCsr(csr, user, caId, until);


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
}
