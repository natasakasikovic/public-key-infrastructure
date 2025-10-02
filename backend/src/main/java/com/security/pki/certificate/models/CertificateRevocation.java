package com.security.pki.certificate.models;

import com.security.pki.certificate.enums.RevocationReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "revocations")
public class CertificateRevocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RevocationReason reason;

    @Column(nullable = false)
    private LocalDateTime revocationDate;
}
