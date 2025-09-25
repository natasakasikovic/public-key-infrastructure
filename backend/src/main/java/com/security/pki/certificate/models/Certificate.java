package com.security.pki.certificate.models;

import com.security.pki.user.models.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Certificate {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String serialNumber;

    @Embedded
    @AttributeOverride(name = "principalName", column = @Column(name = "subject_principal_name", nullable = false))
    private Subject subject;

    @Embedded
    @AttributeOverride(name = "principalName", column = @Column(name = "issuer_principal_name", nullable = false))
    private Issuer issuer;

    @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Certificate parent;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Lob
    @Column(name = "encrypted_private_key")
    private byte[] encryptedPrivateKey;

    @Lob
    @Column(name = "wrapped_dek")
    private byte[] wrappedDek;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @Lob
    @Column(name = "certificate_data")
    private byte[] certificateData; // DER format

    @Column(name = "can_sign", nullable = false)
    private boolean canSign = false;
}