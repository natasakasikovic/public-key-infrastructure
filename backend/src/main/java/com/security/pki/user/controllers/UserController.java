package com.security.pki.user.controllers;

import com.security.pki.user.dtos.LoginRequestDto;
import com.security.pki.user.dtos.LoginResponseDto;
import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> createAccount(
            @Valid @RequestBody RegistrationRequestDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(user));
    }

    @GetMapping("/activation")
    public ResponseEntity<Void> activateUser(@RequestParam("token") String token) {
        service.activateUser(token);
        // TODO: redirect to login once frontend implementation is ready
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(service.login(request));
    }

}
