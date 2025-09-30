package com.security.pki.certificate.repositories;

import com.security.pki.certificate.models.CertificateRevocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRevocationRepository extends JpaRepository<CertificateRevocation, Integer> {
}
