package com.security.pki.certificate.dtos.template;

import com.security.pki.certification.validators.template.ValidRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CertificateTemplateRequestDto {
    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Signing certificate is required")
    private UUID signingCertificateId;

    @ValidRegex(message = "Common name regex must be valid")
    private String commonNameRegex;

    @ValidRegex(message = "SAN regex is mandatory")
    private String sanRegex;

    @NotNull(message = "TTL days is mandatory")
    @Positive(message = "TTL days must be positive")
    private Integer ttlDays;

    private List<@NotBlank(message = "Key usage must not be blank") String> keyUsages;
    private List<@NotBlank(message = "Extended key usage must not be blank") String> extendedKeyUsages;
}
