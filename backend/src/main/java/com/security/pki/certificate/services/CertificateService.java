package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.exceptions.CertificateNotAllowedToSignException;
import com.security.pki.certificate.exceptions.RootCertificateIssuanceNotAllowedException;
import com.security.pki.certificate.mappers.CertificateMapper;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.repositories.CertificateRepository;
import com.security.pki.certificate.validators.CertificateValidationContext;
import com.security.pki.certificate.validators.CertificateValidator;
import com.security.pki.user.enums.Role;
import com.security.pki.user.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository repository;
    private final AuthService authService;
    private final CertificateMapper mapper;
    private final List<CertificateValidator> validators;

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