package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.revocation.RevocationRequestDto;
import com.security.pki.certificate.dtos.revocation.RevocationResponseDto;
import com.security.pki.certificate.models.Certificate;
import com.security.pki.certificate.models.CertificateRevocation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CertificateRevocationMapper {

    public CertificateRevocation toCertificateRevocation(RevocationRequestDto request, Certificate c) {
        return CertificateRevocation.builder()
                    .reason(request.getReason())
                    .certificate(c)
                    .revocationDate(LocalDateTime.now())
                    .build();
    }

    public RevocationResponseDto toCertificateResponseDto(CertificateRevocation revocation) {
        return RevocationResponseDto.builder()
                .revocationReason(revocation.getReason().getLabel())
                .revocationTime(revocation.getRevocationDate())
                .build();
    }

}
