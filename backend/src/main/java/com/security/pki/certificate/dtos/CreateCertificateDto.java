package com.security.pki.certificate.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// NOTE: This DTO is intended for creating End-Entity and Intermediate certificates (next PR) - skip for now
public class CreateCertificateDto {
    private String signingSerialNumber;
    private Date validFrom;
    private Date validTo;
    private Boolean canSign;
}