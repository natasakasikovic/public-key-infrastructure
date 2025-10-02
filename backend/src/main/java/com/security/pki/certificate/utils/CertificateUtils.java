package com.security.pki.certificate.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

public class CertificateUtils {

    private static final Map<String, String> EKU_MAP = Map.ofEntries(
            Map.entry("1.3.6.1.5.5.7.3.1", "TLS Web Server Authentication"),
            Map.entry("1.3.6.1.5.5.7.3.2", "TLS Web Client Authentication"),
            Map.entry("1.3.6.1.5.5.7.3.3", "Code Signing"),
            Map.entry("1.3.6.1.5.5.7.3.4", "Email Protection"),
            Map.entry("1.3.6.1.5.5.7.3.8", "Time Stamping"),
            Map.entry("1.3.6.1.5.5.7.3.9", "OCSP Signing")
    );

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

    public static List<String> mapEkuOids(List<String> oids) {
        return oids.stream()
                .map(oid -> EKU_MAP.getOrDefault(oid, oid))
                .toList();
    }

}