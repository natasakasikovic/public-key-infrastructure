package com.security.pki.certificate.controllers;

import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificateService service;

    @PostMapping
    public ResponseEntity<Void> createCertificate(@RequestBody CreateCertificateDto request) {
        service.createCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}