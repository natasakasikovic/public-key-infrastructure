package com.security.pki.certificate.models;

import com.security.pki.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "certificate_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateTemplate {

    @Id
    private UUID id;

    private String name;

    @Embedded
    private Issuer issuer;

    private String commonNameRegex;

    private String sanRegex;

    private Integer ttlDays;

    @ElementCollection
    @CollectionTable(name = "template_key_usages", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "key_usage")
    private List<String> keyUsages;

    @ElementCollection
    @CollectionTable(name = "template_extended_key_usages", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "extended_key_usage")
    private List<String> extendedKeyUsages;

    @ManyToOne
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
