package com.security.pki.certification.dtos;

import com.security.pki.certification.models.Issuer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificateTemplateDto {
    private Long id;
    private String name;
    private Issuer issuer;
    private String commonNameRegex;
    private String sanRegex;
    private Integer ttlDays;
    private String keyUsage;
    private String extendedKeyUsage;
}
