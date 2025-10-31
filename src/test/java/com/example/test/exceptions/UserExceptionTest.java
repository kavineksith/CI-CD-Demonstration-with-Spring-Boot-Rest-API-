package com.example.test.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserExceptionTest {
    // Concrete implementation for testing the abstract class
    private static class TestUserException extends UserException {
        public TestUserException(String message) {
            super(message);
        }

        public TestUserException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Test
    void testUserException_WithMessage() {
        // Given
        String message = "Test user exception message";

        // When
        TestUserException exception = new TestUserException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserException_WithMessageAndCause() {
        // Given
        String message = "Test user exception message";
        Throwable cause = new RuntimeException("Root cause");

        // When
        TestUserException exception = new TestUserException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUserException_IsRuntimeException() {
        // Given
        TestUserException exception = new TestUserException("Test message");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }
}