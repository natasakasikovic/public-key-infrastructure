package com.security.pki.certificate.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaCertificateDto {
    private UUID id;
    private String commonName;
    private String organization;
    private String serialNumber;
    private Date validFrom;
    private Date validTo;
}
