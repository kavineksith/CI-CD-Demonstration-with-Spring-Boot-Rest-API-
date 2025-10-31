package com.example.test.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionTest {
    @Test
    void testUserNotFoundException_WithMessage() {
        // Given
        String message = "User with email test@example.com not found";

        // When
        UserNotFoundException exception = new UserNotFoundException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserNotFoundException_WithMessageAndCause() {
        // Given
        String message = "User with email test@example.com not found";
        Throwable cause = new RuntimeException("Database connection failed");

        // When
        UserNotFoundException exception = new UserNotFoundException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUserNotFoundException_InheritsFromUserException() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("Test message");

        // Then
        assertTrue(exception instanceof UserException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testUserNotFoundException_WithNullMessage() {
        // When
        UserNotFoundException exception = new UserNotFoundException(null);

        // Then
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserNotFoundException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        UserNotFoundException exception = new UserNotFoundException(message);

        // Then
        assertEquals("", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserNotFoundException_WithNullCause() {
        // Given
        String message = "Test message";

        // When
        UserNotFoundException exception = new UserNotFoundException(message, null);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUserNotFoundException_CauseChaining() {
        // Given
        RuntimeException rootCause = new RuntimeException("Root cause");
        Exception intermediateCause = new Exception("Intermediate cause", rootCause);
        String message = "User not found";

        // When
        UserNotFoundException exception = new UserNotFoundException(message, intermediateCause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }
}