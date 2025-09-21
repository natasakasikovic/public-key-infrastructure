package com.security.pki.user.controllers;

import com.security.pki.user.dtos.LoginRequestDto;
import com.security.pki.user.dtos.LoginResponseDto;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    // TODO: change ret value type
    @PostMapping("/registration")
    public ResponseEntity<Void> createAccount(@Validated @RequestBody RegistrationRequestDto user) {
        service.register(user);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Validated @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(service.login(request));
    }

}
