package com.security.pki.user.services;

import com.security.pki.user.dtos.RegistrationRequestDto;
import com.security.pki.user.mappers.UserMapper;
import com.security.pki.user.models.User;
import com.security.pki.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public void register(RegistrationRequestDto request) {
        User user = mapper.fromRequest(request);
        repository.save(user);
        // TODO: generate a VerificationToken and send an email
    }
}
