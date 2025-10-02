package com.security.pki.shared.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LogFormat {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogFormat() {}

    public static String loginAttempt(String email, String ip) {
        return String.format("LOGIN_ATTEMPT | email=%s | ip=%s | time=%s",
                email, ip, now());
    }

    public static String loginSuccess(String email, String ip, Long userId) {
        return String.format("LOGIN_SUCCESS | email=%s | ip=%s | userId=%d | time=%s",
                email, ip, userId, now());
    }

    public static String loginFailure(String email, String ip, String reason) {
        return String.format("LOGIN_FAILURE | email=%s | ip=%s | reason=%s | time=%s",
                email, ip, reason, now());
    }

    public static String registration(String email, String ip) {
        return String.format("REGISTRATION | email=%s | ip=%s | time=%s",
                email, ip, now());
    }

    public static String activation(String email, String ip) {
        return String.format("ACCOUNT_ACTIVATION | email=%s | ip=%s | time=%s",
                email, ip, now());
    }

    private static String now() {
        return LocalDateTime.now().format(TS);
    }
}
