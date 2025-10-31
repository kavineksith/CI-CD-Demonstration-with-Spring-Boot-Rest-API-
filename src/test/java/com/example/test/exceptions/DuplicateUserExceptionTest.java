package com.example.test.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateUserExceptionTest {
    @Test
    void testDuplicateUserException_WithMessage() {
        // Given
        String message = "User with email test@example.com already exists";

        // When
        DuplicateUserException exception = new DuplicateUserException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testDuplicateUserException_WithMessageAndCause() {
        // Given
        String message = "User with email test@example.com already exists";
        Throwable cause = new RuntimeException("Database constraint violation");

        // When
        DuplicateUserException exception = new DuplicateUserException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testDuplicateUserException_InheritsFromUserException() {
        // Given
        DuplicateUserException exception = new DuplicateUserException("Test message");

        // Then
        assertTrue(exception instanceof UserException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testDuplicateUserException_WithNullMessage() {
        // When
        DuplicateUserException exception = new DuplicateUserException(null);

        // Then
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testDuplicateUserException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        DuplicateUserException exception = new DuplicateUserException(message);

        // Then
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testDuplicateUserException_WithNullCause() {
        // Given
        String message = "Test message";

        // When
        DuplicateUserException exception = new DuplicateUserException(message, null);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}