package com.example.test.dto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDTOTest {

    @Test
    void testDefaultConstructor() {
        // When
        ResponseDTO responseDTO = new ResponseDTO();

        // Then
        assertNull(responseDTO.getId());
        assertNull(responseDTO.getName());
        assertNull(responseDTO.getEmail());
        assertNull(responseDTO.getPassword());
    }

    @Test
    void testParameterizedConstructor_ShouldSetAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String password = "hashedPassword123";

        // When
        ResponseDTO responseDTO = new ResponseDTO(id, name, email, password);

        // Then
        assertEquals(id, responseDTO.getId());
        assertEquals(name, responseDTO.getName());
        assertEquals(email, responseDTO.getEmail());
        assertEquals(password, responseDTO.getPassword());
    }

    @Test
    void testParameterizedConstructor_WithNullValues() {
        // When
        ResponseDTO responseDTO = new ResponseDTO(null, null, null, null);

        // Then
        assertNull(responseDTO.getId());
        assertNull(responseDTO.getName());
        assertNull(responseDTO.getEmail());
        assertNull(responseDTO.getPassword());
    }

    @Test
    void testSetId() {
        // Given
        ResponseDTO responseDTO = new ResponseDTO();
        UUID id = UUID.randomUUID();

        // When
        responseDTO.setId(id);

        // Then
        assertEquals(id, responseDTO.getId());
    }

    @Test
    void testSetId_WithNull() {
        // Given
        ResponseDTO responseDTO = new ResponseDTO();
        UUID initialId = UUID.randomUUID();
        responseDTO.setId(initialId);

        // When
        responseDTO.setId(null);

        // Then
        assertNull(responseDTO.getId());
    }

    @Test
    void testGetId() {
        // Given
        UUID id = UUID.randomUUID();
        ResponseDTO responseDTO = new ResponseDTO(id, "John", "john@example.com", "password");

        // When
        UUID retrievedId = responseDTO.getId();

        // Then
        assertEquals(id, retrievedId);
        assertSame(id, retrievedId);
    }

    @Test
    void testInheritanceFromUserDTO() {
        // Given
        ResponseDTO responseDTO = new ResponseDTO();

        // Then
        assertTrue(responseDTO instanceof UserDTO);
    }

    @Test
    void testSettersFromParentClass() {
        // Given
        ResponseDTO responseDTO = new ResponseDTO();

        // When
        responseDTO.setName("Jane Doe");
        responseDTO.setEmail("jane@example.com");
        responseDTO.setPassword("newPassword123");

        // Then
        assertEquals("Jane Doe", responseDTO.getName());
        assertEquals("jane@example.com", responseDTO.getEmail());
        assertEquals("newPassword123", responseDTO.getPassword());
    }

    @Test
    void testConstructor_WithEmptyStrings() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "";
        String email = "";
        String password = "";

        // When
        ResponseDTO responseDTO = new ResponseDTO(id, name, email, password);

        // Then
        assertEquals(id, responseDTO.getId());
        assertEquals("", responseDTO.getName());
        assertEquals("", responseDTO.getEmail());
        assertEquals("", responseDTO.getPassword());
    }

    @Test
    void testConstructor_WithSpecialCharacters() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "José María";
        String email = "jose.maria@example.com";
        String password = "h@sh3d!P@ssw0rd";

        // When
        ResponseDTO responseDTO = new ResponseDTO(id, name, email, password);

        // Then
        assertEquals(id, responseDTO.getId());
        assertEquals(name, responseDTO.getName());
        assertEquals(email, responseDTO.getEmail());
        assertEquals(password, responseDTO.getPassword());
    }

    @Test
    void testIdPersistence() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ResponseDTO responseDTO = new ResponseDTO();

        // When
        responseDTO.setId(id1);
        UUID firstRetrieval = responseDTO.getId();
        responseDTO.setId(id2);
        UUID secondRetrieval = responseDTO.getId();

        // Then
        assertEquals(id1, firstRetrieval);
        assertEquals(id2, secondRetrieval);
        assertNotEquals(firstRetrieval, secondRetrieval);
    }

    @Test
    void testDefaultConstructor_ThenSetFields() {
        // Given
        ResponseDTO responseDTO = new ResponseDTO();
        UUID id = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";
        String password = "testPassword";

        // When
        responseDTO.setId(id);
        responseDTO.setName(name);
        responseDTO.setEmail(email);
        responseDTO.setPassword(password);

        // Then
        assertEquals(id, responseDTO.getId());
        assertEquals(name, responseDTO.getName());
        assertEquals(email, responseDTO.getEmail());
        assertEquals(password, responseDTO.getPassword());
    }
}