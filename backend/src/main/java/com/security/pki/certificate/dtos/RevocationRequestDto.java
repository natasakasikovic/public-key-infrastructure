package com.security.pki.certificate.dtos;

import com.security.pki.certificate.enums.RevocationReason;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevocationRequestDto {
    @NotNull(message = "Revocation reason is required.")
    private RevocationReason reason;
}
