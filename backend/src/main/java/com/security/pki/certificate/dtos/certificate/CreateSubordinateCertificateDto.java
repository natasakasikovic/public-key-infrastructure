package com.security.pki.certificate.dtos.certificate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubordinateCertificateDto {
    @NotNull(message = "Valid-from date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date validFrom;

    @NotNull(message = "Valid-to date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date validTo;

    @NotNull(message = "Signing certificate is required")
    private UUID signingCertificateId;

    @NotEmpty(message = "Key usages are required")
    private List<String> keyUsages;

    @NotEmpty(message = "Extended key usages are required")
    private List<String> extendedKeyUsages;

    @NotEmpty(message = "Common Name (CN) is required")
    private String commonName;

    @NotEmpty(message = "Country (C) is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 letters (ISO 3166)")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be uppercase ISO code")
    private String country;

    private Long userId;

    private String organizationalUnit;

    private String state;

    private String locality;

    private Integer pathLenConstraint;

    @NotNull
    private Boolean canSign;
}