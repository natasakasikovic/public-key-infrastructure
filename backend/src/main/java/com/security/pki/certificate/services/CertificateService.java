package com.security.pki.certificate.services;

import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.exceptions.CertificateNotAllowedToSignException;
import com.security.pki.certificate.exceptions.RootCertificateIssuanceNotAllowedException;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.user.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;

    public void createCertificate(CreateCertificateDto request) {

        Role sessionUserRole = Role.ADMIN; // TODO: call function instead
        String signingSerialNumber = request.getSigningSerialNumber();
        Certificate signingCertificate = null;

        if (signingSerialNumber != null)
            signingCertificate =findBySerialNumber(signingSerialNumber);

        // TODO: extract code below into validators and add already extracted validators
        if (sessionUserRole != Role.ADMIN && signingCertificate == null)
            throw new RootCertificateIssuanceNotAllowedException("Only ADMIN users are allowed to issue root certificates.");

        if (signingCertificate != null && signingCertificate.isCanSign())
            throw new CertificateNotAllowedToSignException(String.format(
                            "Certificate with serial number %s is not allowed to sign other certificates.",signingCertificate.getSerialNumber()));

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