package com.example.test.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Encryptor Tests")
class PasswordEncryptorTest {

    @Nested
    @DisplayName("Encrypt Method Tests")
    class EncryptMethodTests {
        @Test
        @DisplayName("Should return encrypted password for valid input")
        void testEncrypt_ShouldReturnEncryptedPassword() {
            // Given
            String rawPassword = "testPassword123";

            // When
            String encryptedPassword = PasswordEncryptor.encrypt(rawPassword);

            // Then
            assertNotNull(encryptedPassword);
            assertNotEquals(rawPassword, encryptedPassword);
            assertTrue(encryptedPassword.startsWith("$2a$"));
        }

        @Test
        @DisplayName("Should produce different hashes for different passwords")
        void testEncrypt_WithDifferentPasswords_ShouldProduceDifferentHashes() {
            // Given
            String password1 = "password123";
            String password2 = "password456";

            // When
            String encrypted1 = PasswordEncryptor.encrypt(password1);
            String encrypted2 = PasswordEncryptor.encrypt(password2);

            // Then
            assertNotEquals(encrypted1, encrypted2);
        }

        @Test
        @DisplayName("Should produce different hashes for same password due to salt")
        void testEncrypt_WithSamePassword_ShouldProduceDifferentHashesDueToSalt() {
            // Given
            String rawPassword = "testPassword123";

            // When
            String encrypted1 = PasswordEncryptor.encrypt(rawPassword);
            String encrypted2 = PasswordEncryptor.encrypt(rawPassword);

            // Then
            assertNotEquals(encrypted1, encrypted2); // BCrypt uses random salt
        }

        @Test
        @DisplayName("Should throw exception when password is null")
        void testMatches_WithNullEncryptedPassword_ShouldThrowException() {
            // Given
            String rawPassword = "testPassword123";

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                    PasswordEncryptor.matches(rawPassword, null)
            );
        }
    }

    @Nested
    @DisplayName("Matches Method Tests")
    class MatchesMethodTests {
        @Nested
        @DisplayName("Valid Password Scenarios")
        class ValidPasswordScenarios {
            @Test
            @DisplayName("Should return true for correct password")
            void testMatches_WithCorrectPassword_ShouldReturnTrue() {
                // Given
                String rawPassword = "testPassword123";
                String encryptedPassword = PasswordEncryptor.encrypt(rawPassword);

                // When
                boolean matches = PasswordEncryptor.matches(rawPassword, encryptedPassword);

                // Then
                assertTrue(matches);
            }

            @Test
            @DisplayName("Should return false for incorrect password")
            void testMatches_WithIncorrectPassword_ShouldReturnFalse() {
                // Given
                String rawPassword = "testPassword123";
                String wrongPassword = "wrongPassword";
                String encryptedPassword = PasswordEncryptor.encrypt(rawPassword);

                // When
                boolean matches = PasswordEncryptor.matches(wrongPassword, encryptedPassword);

                // Then
                assertFalse(matches);
            }

            @Test
            @DisplayName("Should return false for empty password")
            void testMatches_WithEmptyRawPassword_ShouldReturnFalse() {
                // Given
                String encryptedPassword = PasswordEncryptor.encrypt("testPassword123");

                // When
                boolean matches = PasswordEncryptor.matches("", encryptedPassword);

                // Then
                assertFalse(matches);
            }
        }

        @Nested
        @DisplayName("Null Input Scenarios")
        class NullInputScenarios {
            @Test
            @DisplayName("Should return false when raw password is null")
            void testMatches_WithNullRawPassword_ShouldReturnFalse() {
                // Given
                String encryptedPassword = PasswordEncryptor.encrypt("testPassword123");

                // When
                boolean matches = PasswordEncryptor.matches(null, encryptedPassword);

                // Then
                assertFalse(matches);
            }

            @Test
            @DisplayName("Should throw exception when encrypted password is null")
            void testMatches_WithNullEncryptedPassword_ShouldThrowException() {
                // Given
                String rawPassword = "testPassword123";

                // When & Then
                assertThrows(IllegalArgumentException.class, () ->
                        PasswordEncryptor.matches(rawPassword, null)
                );
            }
        }
    }












}