package com.security.pki.certificate.repositories;

import com.security.pki.certificate.enums.Status;
import com.security.pki.certificate.models.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    Optional<Certificate> findBySerialNumber(String serialNumber);
    Page<Certificate> findByOwner_Id(Long id, Pageable pageable);
    List<Certificate> findByParent_SerialNumber(String serialNumber);
    List<Certificate> findByOwner_IdAndCanSignTrue(Long ownerId);

    @Query("SELECT c FROM Certificate c WHERE c.canSign = true AND c.status <> :revokedStatus AND :now BETWEEN c.validFrom AND c.validTo")
    Page<Certificate> findValidParentCas(@Param("revokedStatus") Status revokedStatus, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT c FROM Certificate c  WHERE c.canSign = true AND c.status <> :revokedStatus AND :now BETWEEN c.validFrom AND c.validTo AND c.owner.id = :ownerId")
    List<Certificate> findValidCertificatesByOwner(@Param("revokedStatus") Status revokedStatus, @Param("now") LocalDateTime now, @Param("ownerId") Long ownerId);
}