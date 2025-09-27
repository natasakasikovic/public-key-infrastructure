package com.security.pki.certification.mappers;

import com.security.pki.certification.dtos.CertificateTemplateDto;
import com.security.pki.certification.models.CertificateTemplate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificateTemplateMapper {
    private final ModelMapper modelMapper;

    public CertificateTemplate fromRequest(CertificateTemplateDto certificateTemplate) {
        return modelMapper.map(certificateTemplate, CertificateTemplate.class);
    }

    public CertificateTemplateDto toResponse(CertificateTemplate certificateTemplate) {
        return modelMapper.map(certificateTemplate, CertificateTemplateDto.class);
    }

}
