package com.example.test.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncryptor {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encrypt(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encryptedPassword) {
        if (rawPassword == null) {
            return false;
        }
        if (encryptedPassword == null) {
            throw new IllegalArgumentException("Encrypted password cannot be null");
        }
        return encoder.matches(rawPassword, encryptedPassword);
    }
}