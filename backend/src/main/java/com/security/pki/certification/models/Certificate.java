package com.security.pki.certification.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Embedded
    @AttributeOverride(name = "principalName", column = @Column(name = "subject_principal_name", nullable = false))
    private Subject subject;

    @Embedded
    @AttributeOverride(name = "principalName", column = @Column(name = "issuer_principal_name", nullable = false))
    private Issuer issuer;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
}