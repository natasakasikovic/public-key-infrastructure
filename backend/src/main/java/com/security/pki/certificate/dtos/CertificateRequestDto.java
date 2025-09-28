package com.security.pki.certificate.dtos;

import com.security.pki.certificate.CertificateType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CertificateRequestDto {
    // Basic subject info (X500Name)
    private String commonName;          // CN
    private String surname;             // SURNAME
    private String givenName;           // GIVEN NAME
    private String organization;        // O
    private String organizationalUnit;  // OU
    private String country;             // C
    private String email;               // E

    // Owner info
    private Long userId;

    // Validity
    private LocalDate validFrom;
    private LocalDate validTo;

    // Chosen CA (issuer)
    private UUID caCertificateId;

    // Type (Root, Intermediate, EndEntity)
    private CertificateType certificateType;
}
