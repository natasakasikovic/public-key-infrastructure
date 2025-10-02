package com.security.pki.certificate.repositories;

import com.security.pki.certificate.models.CertificateTemplate;
import com.security.pki.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, UUID> {
    List<CertificateTemplate> findByCreatedBy(User createdBy);
}
