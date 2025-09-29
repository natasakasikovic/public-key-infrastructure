package com.security.pki.user.controllers;

import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.dtos.RegistrationResponseDto;
import com.security.pki.user.enums.Role;
import com.security.pki.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UserService service;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationResponseDto> createAccount(
            @Valid @RequestBody RegistrationRequestDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(user, Role.REGULAR_USER));
    }

    @PostMapping("/ca-registration")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RegistrationResponseDto> createCaUserAccount(
            @Valid @RequestBody RegistrationRequestDto user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(user, Role.CA_USER));
    }

    @GetMapping("/activation")
    public ResponseEntity<Void> activateUser(@RequestParam("token") String token) {
        service.activateUser(token);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", frontendUrl + "/login")
                .build();
    }

}
