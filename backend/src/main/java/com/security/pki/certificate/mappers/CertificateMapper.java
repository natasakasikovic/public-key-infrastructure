package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.models.Certificate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CertificateMapper {
    private final ModelMapper modelMapper;

    // NOTE: refactor method below when you add more attr to CreateCertificateDto
    public Certificate fromRequest(CreateCertificateDto request) {
        return modelMapper.map(request, Certificate.class);
    }
}