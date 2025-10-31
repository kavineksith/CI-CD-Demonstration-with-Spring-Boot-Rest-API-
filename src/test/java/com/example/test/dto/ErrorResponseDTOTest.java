package com.example.test.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDTOTest {

    @Test
    void testDefaultConstructor_ShouldSetTimestamp() {
        // Given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // When
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();

        // Then
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isAfter(before));
        assertTrue(errorResponse.getTimestamp().isBefore(after));
    }

    @Test
    void testConstructorWithBasicFields_ShouldSetAllFieldsAndTimestamp() {
        // Given
        int status = 404;
        String error = "Not Found";
        String message = "User not found";
        String path = "/users/123";

        // When
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status, error, message, path);

        // Then
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testConstructorWithDetails_ShouldSetAllFields() {
        // Given
        int status = 400;
        String error = "Bad Request";
        String message = "Validation failed";
        String path = "/users/create";
        List<String> details = Arrays.asList("Name is required", "Email is invalid");

        // When
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(status, error, message, path, details);

        // Then
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertEquals(details, errorResponse.getDetails());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        LocalDateTime timestamp = LocalDateTime.now();
        int status = 500;
        String error = "Internal Server Error";
        String message = "Something went wrong";
        String path = "/users/update";
        List<String> details = Arrays.asList("Database connection failed");

        // When
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(status);
        errorResponse.setError(error);
        errorResponse.setMessage(message);
        errorResponse.setPath(path);
        errorResponse.setDetails(details);

        // Then
        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(error, errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(path, errorResponse.getPath());
        assertEquals(details, errorResponse.getDetails());
    }

    @Test
    void testSetTimestampToNull() {
        // Given
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();

        // When
        errorResponse.setTimestamp(null);

        // Then
        assertNull(errorResponse.getTimestamp());
    }

    @Test
    void testSetNullValues() {
        // Given
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(400, "Bad Request", "Test message", "/test");

        // When
        errorResponse.setError(null);
        errorResponse.setMessage(null);
        errorResponse.setPath(null);
        errorResponse.setDetails(null);

        // Then
        assertNull(errorResponse.getError());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getPath());
        assertNull(errorResponse.getDetails());
        assertEquals(400, errorResponse.getStatus()); // Status should remain unchanged
    }

    @Test
    void testEmptyDetailsList() {
        // Given
        List<String> emptyDetails = Arrays.asList();

        // When
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(400, "Bad Request", "Test", "/test", emptyDetails);

        // Then
        assertEquals(emptyDetails, errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void testModifyDetailsList() {
        // Given
        List<String> details = Arrays.asList("Error 1", "Error 2");
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();

        // When
        errorResponse.setDetails(details);
        List<String> newDetails = Arrays.asList("Error 3", "Error 4", "Error 5");
        errorResponse.setDetails(newDetails);

        // Then
        assertEquals(newDetails, errorResponse.getDetails());
        assertEquals(3, errorResponse.getDetails().size());
        assertEquals("Error 3", errorResponse.getDetails().get(0));
        assertEquals("Error 5", errorResponse.getDetails().get(2));
    }
}