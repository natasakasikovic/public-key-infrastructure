package com.security.pki.certificate.repositories;

import com.security.pki.certificate.models.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    Optional<Certificate> findBySerialNumber(String serialNumber);
    Page<Certificate> findByOwner_Id(Long id, Pageable pageable);
    List<Certificate> findByParent_SerialNumber(String serialNumber);
    List<Certificate> findByOwner_IdAndCanSignTrue(Long ownerId);
}