package com.security.pki.certificate.models;

import com.security.pki.certificate.enums.SANType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class SAN {
    @Enumerated(EnumType.STRING)
    private SANType type;
    private String value;
}
