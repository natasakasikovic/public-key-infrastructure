package com.security.pki.certificate.dtos.certificate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyDto {
    private String principalName;
    private String email;
    private String country;
    private String organizationUnit;
    private String organizationName;
    private String commonName;
}
