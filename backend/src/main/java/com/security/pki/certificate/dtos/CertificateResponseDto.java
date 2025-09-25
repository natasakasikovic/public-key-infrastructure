package com.security.pki.certificate.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificateResponseDto {
    private String serialNumber;
    private String certificateType;
    private String issuerEmail;
    private String subjectEmail;
}