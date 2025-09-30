package com.security.pki.certificate.services;

import com.security.pki.certificate.dtos.RevocationRequestDto;
import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.exceptions.RevocationException;
import com.security.pki.certificate.mappers.CertificateRevocationMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.repositories.CertificateRevocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevocationService {

    private final CertificateRepository certificateRepository;
    private final CertificateRevocationRepository repository;
    private final CertificateRevocationMapper mapper;

    public void revoke(UUID certificateId, RevocationRequestDto request) {
        Certificate certificate = certificateRepository.findById(certificateId).orElse(null);

        if (certificate == null)
            throw new EntityNotFoundException("Certificate not found.");


        if (certificate.getStatus() == Status.REVOKED)
            throw new RevocationException("Certificate already revoked.");

        certificate.setStatus(Status.REVOKED);
        certificateRepository.save(certificate);
        repository.save(mapper.toCertificateRevocation(request, certificate));

        updateCrl();
    }

    private void updateCrl() {
    }

}
