package com.security.pki.certificate.repositories;

import com.security.pki.certificate.models.CertificateRevocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRevocationRepository extends JpaRepository<CertificateRevocation, Integer> {
    List<CertificateRevocation> findAllByCertificate_Owner_Id(Long id);
}
