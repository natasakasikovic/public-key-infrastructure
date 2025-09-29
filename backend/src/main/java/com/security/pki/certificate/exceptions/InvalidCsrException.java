package com.security.pki.certificate.exceptions;

public class InvalidCsrException extends RuntimeException {
    public InvalidCsrException(String message) {
      super(message);
    }
}
