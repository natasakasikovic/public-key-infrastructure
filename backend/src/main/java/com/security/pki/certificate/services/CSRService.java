package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CreateSubordinateCertificateDto;
import com.security.pki.certificate.exceptions.InvalidCsrException;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.security.PublicKey;

@Service
@RequiredArgsConstructor
public class CSRService {

    private final CertificateService certificateService;
    private final CertificateMapper mapper;
    private final AuthService authService;

    public void processCsrUpload(String caId, String until, MultipartFile csrFile) {
        PKCS10CertificationRequest csr = loadCsr(csrFile);
        User user = authService.getCurrentUser();
        PublicKey publicKey = extractKey(csr);

        CreateSubordinateCertificateDto request = mapper.fromCsr(csr, user, caId, until);


        // invoke certificate creation
        // certificateService.createEndEntityCertificate(request, publicKey);
        System.out.println("Certificate request: " + request);
    }

    private PKCS10CertificationRequest loadCsr(MultipartFile file) {
        try (PEMParser parser = new PEMParser(new InputStreamReader(file.getInputStream()))) {
            Object parsed = parser.readObject();
            if (parsed instanceof PKCS10CertificationRequest csr)
                return csr;

            throw new InvalidCsrException("Provided file is not a valid CSR");
        } catch (Exception e) {
            throw new InvalidCsrException("Failed to parse CSR file");
        }
    }

    private PublicKey extractKey(PKCS10CertificationRequest csr) {
        try {
            return new JcaPEMKeyConverter().getPublicKey(csr.getSubjectPublicKeyInfo());
        } catch (Exception e) {
            throw new InvalidCsrException("Failed to extract public key from csr");
        }
    }
}
