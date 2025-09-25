package com.security.pki.certificate.controllers;

import com.security.pki.certificate.dtos.CreateCertificateDto;
import com.security.pki.certificate.dtos.CreateRootCertificateRequest;
import com.security.pki.certificate.services.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.OperatorCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

@RestController
@RequestMapping("/api/v1/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService service;

    @PostMapping("/root")
    public ResponseEntity<String> createRootCertificate(@Valid @RequestBody CreateRootCertificateRequest request) throws NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, CertificateException, CertIOException {
        service.createRootCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping
    public ResponseEntity<Void> createCertificate(@RequestBody CreateCertificateDto request) {
        service.createCertificate(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}