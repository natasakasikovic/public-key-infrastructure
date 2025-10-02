package com.security.pki.certificate.dtos.certificate;

import com.security.pki.certificate.enums.CertificateType;
import com.security.pki.certificate.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponseDto {
    private UUID id;
    private String serialNumber;
    private CertificateType certificateType;
    private String issuerEmail;
    private String subjectEmail;
    private Status status;
}