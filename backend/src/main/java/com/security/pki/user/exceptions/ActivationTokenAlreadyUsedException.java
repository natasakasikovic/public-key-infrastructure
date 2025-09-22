package com.security.pki.user.exceptions;

public class ActivationTokenAlreadyUsedException extends RuntimeException {
    public ActivationTokenAlreadyUsedException(String message) {
        super(message);
    }
}
