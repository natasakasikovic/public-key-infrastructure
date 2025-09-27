package com.security.pki.certificate.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class CertificateUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";


    public static BigInteger generateSerialNumber() {
        return new BigInteger(128, RANDOM);
    }

    public static String generatePassword(int length) {
        if (length < 8)
            throw new IllegalArgumentException("Password length must be at least 8 characters");

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}