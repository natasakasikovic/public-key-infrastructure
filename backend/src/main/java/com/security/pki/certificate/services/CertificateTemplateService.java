package com.security.pki.certificate.services;

import com.security.pki.auth.services.AuthService;
import com.security.pki.certificate.dtos.CertificateTemplateDto;
import com.security.pki.certificate.mappers.CertificateTemplateMapper;
import com.security.pki.certificate.models.CertificateTemplate;
import com.security.pki.certificate.repositories.CertificateTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateTemplateService {

    private final AuthService authService;

    private final CertificateTemplateRepository repository;

    private final CertificateTemplateMapper mapper;

    public CertificateTemplateDto createTemplate(CertificateTemplateDto request) {
        CertificateTemplate template = mapper.fromRequest(request);
        template.setCreatedBy(authService.getCurrentUser());
        return mapper.toResponse(repository.save(template));
    }

    public CertificateTemplateDto getTemplate(Long id) {
        return mapper.toResponse(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found")));
    }

    public List<CertificateTemplateDto> getTemplates() {
        return repository.findByCreatedBy(authService.getCurrentUser())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public List<CertificateTemplateDto> getTemplatesByIssuer(String name) {
        return repository.findByIssuer(name).stream().map(mapper::toResponse).toList();
    }

    public CertificateTemplateDto updateTemplate(Long id, CertificateTemplateDto request) {
        CertificateTemplate existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
        CertificateTemplate updated = CertificateTemplate.builder()
                .id(existing.getId())
                .name(request.getName())
                .issuer(request.getIssuer())
                .commonNameRegex(request.getCommonNameRegex())
                .sanRegex(request.getSanRegex())
                .ttlDays(request.getTtlDays())
                .keyUsages(request.getKeyUsages())
                .extendedKeyUsages(request.getExtendedKeyUsages())
                .build();
        return mapper.toResponse(repository.save(updated));
    }

    public void deleteTemplate(Long id) {
        repository.deleteById(id);
    }

}
