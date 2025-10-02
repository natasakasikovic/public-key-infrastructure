package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.template.CertificateTemplateRequestDto;
import com.security.pki.certificate.dtos.template.CertificateTemplateResponseDto;
import com.security.pki.certificate.mappers.CertificateTemplateMapper;
import com.security.pki.certificate.models.CertificateTemplate;
import com.security.pki.certificate.repositories.CertificateTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateTemplateService {

    private final AuthService authService;

    private final CertificateTemplateRepository repository;

    private final CertificateTemplateMapper mapper;

    public CertificateTemplateResponseDto createTemplate(CertificateTemplateRequestDto request) {
        CertificateTemplate template = mapper.fromRequest(request);
        template.setId(UUID.randomUUID());
        template.setCreatedBy(authService.getCurrentUser());
        return mapper.toResponse(repository.save(template));
    }

    public CertificateTemplateResponseDto getTemplate(UUID id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found")));
    }

    public List<CertificateTemplateResponseDto> getTemplates() {
        return repository.findByCreatedBy(authService.getCurrentUser())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<CertificateTemplateResponseDto> getTemplatesByIssuer(UUID id) {
        return repository.findBySigningCertificateId(id).stream().map(mapper::toResponse).toList();
    }

    public CertificateTemplateResponseDto updateTemplate(UUID id, CertificateTemplateRequestDto request) {
        CertificateTemplate existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
        existing.setName(request.getName());
        existing.setSigningCertificateId(request.getSigningCertificateId());
        existing.setCommonNameRegex(request.getCommonNameRegex());
        existing.setSanRegex(request.getSanRegex());
        existing.setTtlDays(request.getTtlDays());
        existing.setKeyUsages(request.getKeyUsages());
        existing.setExtendedKeyUsages(request.getExtendedKeyUsages());
        return mapper.toResponse(repository.save(existing));
    }

    public void deleteTemplate(UUID id) {
        repository.deleteById(id);
    }

}
