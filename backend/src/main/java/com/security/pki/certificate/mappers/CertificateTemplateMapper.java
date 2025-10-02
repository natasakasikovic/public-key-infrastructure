package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.template.CertificateTemplateRequestDto;
import com.security.pki.certificate.dtos.template.CertificateTemplateResponseDto;
import com.security.pki.certificate.models.CertificateTemplate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificateTemplateMapper {
    private final ModelMapper modelMapper;

    public CertificateTemplate fromRequest(CertificateTemplateRequestDto certificateTemplate) {
        return modelMapper.map(certificateTemplate, CertificateTemplate.class);
    }

    public CertificateTemplateResponseDto toResponse(CertificateTemplate certificateTemplate) {
        return CertificateTemplateResponseDto.builder()
                .id(certificateTemplate.getId())
                .name(certificateTemplate.getName())
                .signingCertificateId(certificateTemplate.getSigningCertificateId())
                .issuerEmail(certificateTemplate.getCreatedBy() != null ? certificateTemplate.getCreatedBy().getEmail() : null)
                .commonNameRegex(certificateTemplate.getCommonNameRegex())
                .sanRegex(certificateTemplate.getSanRegex())
                .ttlDays(certificateTemplate.getTtlDays())
                .keyUsages(certificateTemplate.getKeyUsages())
                .extendedKeyUsages(certificateTemplate.getExtendedKeyUsages())
                .build();
    }


}
