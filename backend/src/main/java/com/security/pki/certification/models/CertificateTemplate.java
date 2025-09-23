package com.security.pki.certification.models;

import com.security.pki.user.models.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificate_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Embedded
    private Issuer issuer;
    private String commonNameRegex;
    private String sanRegex;
    private Integer ttlDays;
    private String keyUsage;
    private String extendedKeyUsage;
    @ManyToOne
    private User createdBy; // TODO: Change to CA_USER?
}