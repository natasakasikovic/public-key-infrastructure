package com.security.pki.certification.controllers;

import com.security.pki.certification.dtos.CertificateTemplateDto;
import com.security.pki.certification.services.CertificateTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class CertificateTemplateController {

    private final CertificateTemplateService service;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateDto> createTemplate(@Valid @RequestBody CertificateTemplateDto request) {
        return new ResponseEntity<>(service.createTemplate(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateDto> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplate(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<List<CertificateTemplateDto>> getTemplates() {
        return ResponseEntity.ok(service.getTemplates());
    }

    @GetMapping("/issuer/{name}")
    public ResponseEntity<List<CertificateTemplateDto>> getByIssuer(@PathVariable String name) {
        return ResponseEntity.ok(service.getTemplatesByIssuer(name));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<CertificateTemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CertificateTemplateDto request
    ) {
        return ResponseEntity.ok(service.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CA_USER')")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        service.deleteTemplate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
