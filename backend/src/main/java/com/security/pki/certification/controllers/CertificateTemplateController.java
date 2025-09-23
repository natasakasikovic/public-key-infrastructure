package com.security.pki.certification.controllers;

import com.security.pki.certification.dtos.CertificateTemplateDto;
import com.security.pki.certification.models.CertificateTemplate;
import com.security.pki.certification.services.CertificateTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class CertificateTemplateController {

    private final CertificateTemplateService service;

    @PostMapping
    public ResponseEntity<Void> createTemplate(@Valid @RequestBody CertificateTemplateDto request) {
        service.createTemplate(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateTemplateDto> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTemplate(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateTemplateDto> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody CertificateTemplateDto request
    ) {
        return ResponseEntity.ok(service.updateTemplate(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        service.deleteTemplate(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
