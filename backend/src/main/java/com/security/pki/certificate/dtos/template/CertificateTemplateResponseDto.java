package com.security.pki.certificate.dtos.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateTemplateResponseDto {
    private UUID id;
    private String name;
    private UUID signingCertificateId;
    private String issuerEmail;
    private String commonNameRegex;
    private String sanRegex;
    private Integer ttlDays;
    private List<String> keyUsages;
    private List<String> extendedKeyUsages;
}
