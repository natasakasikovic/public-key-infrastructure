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

    public List<CertificateTemplateResponseDto> getTemplatesByIssuer(String name) {
        return null;
    }

    public CertificateTemplateResponseDto updateTemplate(UUID id, CertificateTemplateRequestDto request) {
        CertificateTemplate existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
        CertificateTemplate updated = CertificateTemplate.builder()
                .id(existing.getId())
                .name(request.getName())
                .signingCertificateId(request.getSigningCertificateId())
                .commonNameRegex(request.getCommonNameRegex())
                .sanRegex(request.getSanRegex())
                .ttlDays(request.getTtlDays())
                .keyUsages(request.getKeyUsages())
                .extendedKeyUsages(request.getExtendedKeyUsages())
                .build();
        return mapper.toResponse(repository.save(updated));
    }

    public void deleteTemplate(UUID id) {
        repository.deleteById(id);
    }

}
