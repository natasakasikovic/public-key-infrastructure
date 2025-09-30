package com.security.pki.certificate.mappers;

import com.security.pki.certificate.dtos.RevocationRequestDto;
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

}
