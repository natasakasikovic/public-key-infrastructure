package com.security.pki.certificate.dtos;

import com.security.pki.certificate.models.CertificateType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateDetailsResponseDto {
    private String serialNumber;
    private PartyDto subject;
    private PartyDto issuer;
    private Date validFrom;
    private Date validTo;
    private CertificateType certificateType;
    private String status;
    private boolean canSign;
    private String ownerEmail;
    private List<String> keyUsages;
    private List<String> extendedKeyUsages;
}
