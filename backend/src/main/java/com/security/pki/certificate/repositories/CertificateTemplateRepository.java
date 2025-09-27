package com.security.pki.certificate.repositories;

import com.security.pki.certificate.models.CertificateTemplate;
import com.security.pki.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
    List<CertificateTemplate> findByCreatedBy(User createdBy);
    List<CertificateTemplate> findByIssuer(String issuer);
}
