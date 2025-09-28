package com.security.pki.certificate.dtos;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
// NOTE: This DTO is intended for creating End-Entity and Intermediate certificates (next PR) - skip for now
public class CreateCertificateDto {
    private String signingSerialNumber;
    private Date validFrom;
    private Date validTo;
    private Boolean canSign;
}