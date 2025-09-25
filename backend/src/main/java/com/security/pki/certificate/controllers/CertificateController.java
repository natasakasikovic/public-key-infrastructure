package com.security.pki.certificate.controllers;

import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.services.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService service;

    @PostMapping("/root")
    public ResponseEntity<String> createRootCertificate(@Valid @RequestBody CreateRootCertificateRequest request) {
        service.createRootCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping
    public ResponseEntity<Void> createCertificate(@RequestBody CreateCertificateDto request) {
        service.createCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{serialNumber}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable String serialNumber) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"keystore.p12\"")
                .body(service.exportAsPkcs12(serialNumber));
    }
}