package com.example.test.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultConstructor() {
        // When
        User user = new User();

        // Then
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    void testConstructorWithNameEmailPassword() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "hashedPassword123";

        // When
        User user = new User(name, email, password);

        // Then
        assertNull(user.getId()); // ID is not set in this constructor
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testConstructorWithAllFields() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "John Doe";
        String email = "john@example.com";
        String password = "hashedPassword123";

        // When
        User user = new User(id, name, email, password);

        // Then
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testConstructorWithNullValues() {
        // When
        User user = new User(null, null, null);

        // Then
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    void testConstructorWithAllNullValues() {
        // When
        User user = new User(null, null, null, null);

        // Then
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }

    @Test
    void testSetId() {
        // Given
        User user = new User();
        UUID id = UUID.randomUUID();

        // When
        user.setId(id);

        // Then
        assertEquals(id, user.getId());
    }

    @Test
    void testSetIdToNull() {
        // Given
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);

        // When
        user.setId(null);

        // Then
        assertNull(user.getId());
    }

    @Test
    void testSetName() {
        // Given
        User user = new User();
        String name = "Jane Doe";

        // When
        user.setName(name);

        // Then
        assertEquals(name, user.getName());
    }

    @Test
    void testSetNameToNull() {
        // Given
        User user = new User("John", "john@example.com", "password");

        // When
        user.setName(null);

        // Then
        assertNull(user.getName());
    }

    @Test
    void testSetEmail() {
        // Given
        User user = new User();
        String email = "jane@example.com";

        // When
        user.setEmail(email);

        // Then
        assertEquals(email, user.getEmail());
    }

    @Test
    void testSetEmailToNull() {
        // Given
        User user = new User("John", "john@example.com", "password");

        // When
        user.setEmail(null);

        // Then
        assertNull(user.getEmail());
    }

    @Test
    void testSetPassword() {
        // Given
        User user = new User();
        String password = "newHashedPassword";

        // When
        user.setPassword(password);

        // Then
        assertEquals(password, user.getPassword());
    }

    @Test
    void testSetPasswordToNull() {
        // Given
        User user = new User("John", "john@example.com", "password");

        // When
        user.setPassword(null);

        // Then
        assertNull(user.getPassword());
    }

    @Test
    void testGettersAfterConstruction() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Test User";
        String email = "test@example.com";
        String password = "testPassword";

        // When
        User user = new User(id, name, email, password);

        // Then
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertSame(id, user.getId()); // Test reference equality
    }

    @Test
    void testSettersOverridePreviousValues() {
        // Given
        User user = new User("Initial Name", "initial@example.com", "initialPassword");
        UUID newId = UUID.randomUUID();
        String newName = "Updated Name";
        String newEmail = "updated@example.com";
        String newPassword = "updatedPassword";

        // When
        user.setId(newId);
        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);

        // Then
        assertEquals(newId, user.getId());
        assertEquals(newName, user.getName());
        assertEquals(newEmail, user.getEmail());
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Given
        String name = "";
        String email = "";
        String password = "";

        // When
        User user = new User(name, email, password);

        // Then
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
    }

    @Test
    void testConstructorWithWhitespaceStrings() {
        // Given
        String name = "   ";
        String email = "   ";
        String password = "   ";

        // When
        User user = new User(name, email, password);

        // Then
        assertEquals("   ", user.getName());
        assertEquals("   ", user.getEmail());
        assertEquals("   ", user.getPassword());
    }

    @Test
    void testSettersWithEmptyStrings() {
        // Given
        User user = new User();

        // When
        user.setName("");
        user.setEmail("");
        user.setPassword("");

        // Then
        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPassword());
    }

    @Test
    void testUUIDPersistence() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        User user = new User();

        // When
        user.setId(id1);
        UUID retrievedId1 = user.getId();
        user.setId(id2);
        UUID retrievedId2 = user.getId();

        // Then
        assertEquals(id1, retrievedId1);
        assertEquals(id2, retrievedId2);
        assertNotEquals(retrievedId1, retrievedId2);
    }

    @Test
    void testMixedConstructorAndSetters() {
        // Given
        User user = new User("Initial", "initial@example.com", "initialPass");

        // When
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setName("Updated");

        // Then
        assertEquals(id, user.getId());
        assertEquals("Updated", user.getName());
        assertEquals("initial@example.com", user.getEmail()); // Unchanged
        assertEquals("initialPass", user.getPassword()); // Unchanged
    }
}