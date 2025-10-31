package com.example.test.unit;

import com.example.test.config.PasswordEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncryptorTest {

    @Test
    @DisplayName("Should encrypt password successfully")
    void shouldEncryptPassword() {
        String rawPassword = "Password123!";
        String encrypted = PasswordEncryptor.encrypt(rawPassword);

        assertNotNull(encrypted);
        assertNotEquals(rawPassword, encrypted);
        assertTrue(encrypted.startsWith("$2a$") || encrypted.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Should match raw password with encrypted password")
    void shouldMatchPasswords() {
        String rawPassword = "Password123!";
        String encrypted = PasswordEncryptor.encrypt(rawPassword);

        assertTrue(PasswordEncryptor.matches(rawPassword, encrypted));
    }

    @Test
    @DisplayName("Should not match incorrect password")
    void shouldNotMatchIncorrectPassword() {
        String rawPassword = "Password123!";
        String wrongPassword = "WrongPassword123!";
        String encrypted = PasswordEncryptor.encrypt(rawPassword);

        assertFalse(PasswordEncryptor.matches(wrongPassword, encrypted));
    }

    @Test
    @DisplayName("Should return false when raw password is null")
    void shouldReturnFalseForNullRawPassword() {
        String encrypted = PasswordEncryptor.encrypt("Password123!");

        assertFalse(PasswordEncryptor.matches(null, encrypted));
    }

    @Test
    @DisplayName("Should throw exception when encrypted password is null")
    void shouldThrowExceptionForNullEncryptedPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordEncryptor.matches("Password123!", null);
        });
    }
}