package com.security.pki.certification.services;

import com.security.pki.certification.dtos.CertificateTemplateDto;
import com.security.pki.certification.mappers.CertificateTemplateMapper;
import com.security.pki.certification.models.CertificateTemplate;
import com.security.pki.certification.repositories.CertificateTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateTemplateService {

    private final CertificateTemplateRepository repository;

    private final CertificateTemplateMapper mapper;

    public void createTemplate(CertificateTemplateDto request) {
        repository.save(mapper.fromCertificateTemplate(request));
    }

    public CertificateTemplateDto getTemplate(Long id) {
        return mapper.toCertificateTemplateDto(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found")));
    }

    public CertificateTemplateDto updateTemplate(Long id, CertificateTemplateDto request) {
        CertificateTemplate existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found"));
        existing.setName(request.getName());
        existing.setIssuer(request.getIssuer());
        existing.setCommonNameRegex(request.getCommonNameRegex());
        existing.setSanRegex(request.getSanRegex());
        existing.setTtlDays(request.getTtlDays());
        existing.setKeyUsage(request.getKeyUsage());
        existing.setExtendedKeyUsage(request.getExtendedKeyUsage());
        return mapper.toCertificateTemplateDto(repository.save(existing));
    }

    public void deleteTemplate(Long id) {
        repository.deleteById(id);
    }

}
