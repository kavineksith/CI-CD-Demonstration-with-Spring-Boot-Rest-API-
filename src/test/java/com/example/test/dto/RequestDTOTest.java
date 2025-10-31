package com.example.test.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestDTOTest {

    @Test
    void testConstructor_ShouldSetAllFields() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "Password123!";

        // When
        RequestDTO requestDTO = new RequestDTO(name, email, password);

        // Then
        assertEquals(name, requestDTO.getName());
        assertEquals(email, requestDTO.getEmail());
        assertEquals(password, requestDTO.getPassword());
    }

    @Test
    void testConstructor_WithNullValues() {
        // When
        RequestDTO requestDTO = new RequestDTO(null, null, null);

        // Then
        assertNull(requestDTO.getName());
        assertNull(requestDTO.getEmail());
        assertNull(requestDTO.getPassword());
    }

    @Test
    void testConstructor_WithEmptyStrings() {
        // Given
        String name = "";
        String email = "";
        String password = "";

        // When
        RequestDTO requestDTO = new RequestDTO(name, email, password);

        // Then
        assertEquals("", requestDTO.getName());
        assertEquals("", requestDTO.getEmail());
        assertEquals("", requestDTO.getPassword());
    }

    @Test
    void testInheritanceFromUserDTO() {
        // Given
        RequestDTO requestDTO = new RequestDTO("John", "john@example.com", "Password123!");

        // Then
        assertTrue(requestDTO instanceof UserDTO);
    }

    @Test
    void testSettersFromParentClass() {
        // Given
        RequestDTO requestDTO = new RequestDTO("Initial", "initial@example.com", "InitialPass123!");

        // When
        requestDTO.setName("Updated Name");
        requestDTO.setEmail("updated@example.com");
        requestDTO.setPassword("UpdatedPass123!");

        // Then
        assertEquals("Updated Name", requestDTO.getName());
        assertEquals("updated@example.com", requestDTO.getEmail());
        assertEquals("UpdatedPass123!", requestDTO.getPassword());
    }

    @Test
    void testConstructor_WithSpecialCharacters() {
        // Given
        String name = "José María";
        String email = "jose.maria@example.com";
        String password = "Pássw0rd!@#$";

        // When
        RequestDTO requestDTO = new RequestDTO(name, email, password);

        // Then
        assertEquals(name, requestDTO.getName());
        assertEquals(email, requestDTO.getEmail());
        assertEquals(password, requestDTO.getPassword());
    }

    @Test
    void testConstructor_WithWhitespaceValues() {
        // Given
        String name = "   John Doe   ";
        String email = " john@example.com ";
        String password = " Password123! ";

        // When
        RequestDTO requestDTO = new RequestDTO(name, email, password);

        // Then
        assertEquals("   John Doe   ", requestDTO.getName());
        assertEquals(" john@example.com ", requestDTO.getEmail());
        assertEquals(" Password123! ", requestDTO.getPassword());
    }
}