package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.CertificateResponseDto;
import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.shared.models.PagedResponse;
import com.security.pki.user.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificateMapper {
    private final ModelMapper modelMapper;

    // NOTE: refactor method below when you add more attr to CreateCertificateDto
    public Certificate fromRequest(CreateCertificateDto request) {
        return modelMapper.map(request, Certificate.class);
    }

    public CertificateResponseDto toResponse(Certificate certificate) {
        CertificateResponseDto response = modelMapper.map(certificate, CertificateResponseDto.class);
        if (certificate.getParent() != null)
            response.setIssuerEmail(certificate.getParent().getOwner().getEmail());
        else
            response.setIssuerEmail(certificate.getOwner().getEmail());
        response.setSubjectEmail(certificate.getOwner().getEmail());
        return response;
    }

    public PagedResponse<CertificateResponseDto> toPagedResponse(Page<Certificate> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}