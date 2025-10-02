package com.security.pki.certificate.controllers;

import com.security.pki.certificate.dtos.template.CertificateTemplateRequestDto;
import com.security.pki.certificate.dtos.template.CertificateTemplateResponseDto;
import com.security.pki.certificate.services.CertificateTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class CertificateTemplateController {

    private final CertificateTemplateService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateResponseDto> createTemplate(@Valid @RequestBody CertificateTemplateRequestDto request) {
        return new ResponseEntity<>(service.createTemplate(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateResponseDto> getTemplate(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getTemplate(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<List<CertificateTemplateResponseDto>> getTemplates() {
        return ResponseEntity.ok(service.getTemplates());
    }

    @GetMapping("/issuer/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<List<CertificateTemplateResponseDto>> getByIssuer(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getTemplatesByIssuer(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateResponseDto> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody CertificateTemplateRequestDto request
    ) {
        return ResponseEntity.ok(service.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        service.deleteTemplate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
