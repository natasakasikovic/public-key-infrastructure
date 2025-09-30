package com.security.pki.certificate.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevocationResponseDto {
    private String revocationReason;
    private LocalDateTime revocationTime;
}
