package com.security.pki.certificate.controllers;

import com.security.pki.certificate.dtos.*;
import com.security.pki.certificate.services.CertificateService;
import com.security.pki.certificate.services.RevocationService;
import com.security.pki.shared.models.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService service;
    private final RevocationService revocationService;

    @PostMapping("/root")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createRootCertificate(@Valid @RequestBody CreateRootCertificateRequest request) throws Exception {
        service.createRootCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/subordinate")
    public ResponseEntity<Void> createSubordinateCertificate(@RequestBody CreateSubordinateCertificateDto request) {
        service.createSubordinateCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDetailsResponseDto> getCertificate(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getCertificate(id));
    }

    @GetMapping("/{serialNumber}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable String serialNumber) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"keystore.p12\"")
                .body(service.exportAsPkcs12(serialNumber));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<CertificateResponseDto>> getCertificates(Pageable pageable) {
        return ResponseEntity.ok(service.getCertificates(pageable));
    }

    @PostMapping("/{id}/revocation")
    public ResponseEntity<RevocationResponseDto> revokeCertificate(
            @PathVariable UUID id,
            @Valid @RequestBody RevocationRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(revocationService.revoke(id, request));
    }

    @GetMapping("/{serialNumber}/crl")
    public ResponseEntity<Resource> getCrl(@PathVariable String serialNumber) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(revocationService.getCrl(serialNumber));
    }
}