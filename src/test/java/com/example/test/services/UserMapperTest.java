package com.example.test.services;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.example.test.config.PasswordEncryptor.encrypt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToEntity_WithValidDTO_ShouldReturnUserWithEncryptedPassword() {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "john@example.com", "Password123!");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encrypted";
            mockedEncryptor.when(() -> encrypt("Password123!")).thenReturn(encryptedPassword);

            // When
            User user = userMapper.toEntity(dto);

            // Then
            assertNotNull(user);
            assertEquals("John Doe", user.getName());
            assertEquals("john@example.com", user.getEmail());
            assertEquals(encryptedPassword, user.getPassword());
            assertNull(user.getId()); // ID should not be set in toEntity method

            mockedEncryptor.verify(() -> encrypt("Password123!"));
        }
    }

    @Test
    void testToEntity_WithEmptyName_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("", "john@example.com", "Password123!");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithWhitespaceName_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("   ", "john@example.com", "Password123!");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithEmptyEmail_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "", "Password123!");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithWhitespaceEmail_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "   ", "Password123!");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithEmptyPassword_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "john@example.com", "");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithWhitespacePassword_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "john@example.com", "   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToEntity_WithAllEmptyFields_ShouldThrowIllegalArgumentException() {
        // Given
        RequestDTO dto = new RequestDTO("", "", "");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userMapper.toEntity(dto)
        );

        assertEquals("All fields are required", exception.getMessage());
    }

    @Test
    void testToUpdateEntity_WithAllFields_ShouldUpdateAllFields() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("New Name", "new@example.com", "newPassword");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encryptedNew";
            mockedEncryptor.when(() -> encrypt("newPassword")).thenReturn(encryptedPassword);

            // When
            userMapper.toUpdateEntity(existingUser, dto);

            // Then
            assertEquals("New Name", existingUser.getName());
            assertEquals("new@example.com", existingUser.getEmail());
            assertEquals(encryptedPassword, existingUser.getPassword());

            mockedEncryptor.verify(() -> encrypt("newPassword"));
        }
    }

    @Test
    void testToUpdateEntity_WithOnlyName_ShouldUpdateOnlyName() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("New Name", null, null);

        // When
        userMapper.toUpdateEntity(existingUser, dto);

        // Then
        assertEquals("New Name", existingUser.getName());
        assertEquals("old@example.com", existingUser.getEmail()); // Unchanged
        assertEquals("oldPassword", existingUser.getPassword()); // Unchanged
    }

    @Test
    void testToUpdateEntity_WithOnlyEmail_ShouldUpdateOnlyEmail() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO(null, "new@example.com", null);

        // When
        userMapper.toUpdateEntity(existingUser, dto);

        // Then
        assertEquals("Old Name", existingUser.getName()); // Unchanged
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals("oldPassword", existingUser.getPassword()); // Unchanged
    }

    void testToUpdateEntity_WithOnlyPassword_ShouldUpdateOnlyPassword() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO(null, null, "newPassword");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encryptedNew";
            mockedEncryptor.when(() -> encrypt("newPassword")).thenReturn(encryptedPassword);

            // When
            userMapper.toUpdateEntity(existingUser, dto);

            // Then
            assertEquals("Old Name", existingUser.getName()); // Unchanged
            assertEquals("old@example.com", existingUser.getEmail()); // Unchanged
            assertEquals(encryptedPassword, existingUser.getPassword());

            mockedEncryptor.verify(() -> encrypt("newPassword"));
        }
    }

    @Test
    void testToUpdateEntity_WithEmptyStrings_ShouldNotUpdate() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("", "", "");

        // When
        userMapper.toUpdateEntity(existingUser, dto);

        // Then
        assertEquals("Old Name", existingUser.getName()); // Unchanged
        assertEquals("old@example.com", existingUser.getEmail()); // Unchanged
        assertEquals("oldPassword", existingUser.getPassword()); // Unchanged
    }

    @Test
    void testToUpdateEntity_WithWhitespaceStrings_ShouldNotUpdate() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("   ", "   ", "   ");

        // When
        userMapper.toUpdateEntity(existingUser, dto);

        // Then
        assertEquals("Old Name", existingUser.getName()); // Unchanged
        assertEquals("old@example.com", existingUser.getEmail()); // Unchanged
        assertEquals("oldPassword", existingUser.getPassword()); // Unchanged
    }

    @Test
    void testToUpdateEntity_WithMixedNullAndValidValues_ShouldUpdateOnlyValidValues() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("New Name", null, "");

        // When
        userMapper.toUpdateEntity(existingUser, dto);

        // Then
        assertEquals("New Name", existingUser.getName());
        assertEquals("old@example.com", existingUser.getEmail()); // Unchanged
        assertEquals("oldPassword", existingUser.getPassword()); // Unchanged
    }

    @Test
    void testToPreviewUser_ShouldReturnResponseDTOWithAllFields() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "John Doe", "john@example.com", "hashedPassword");

        // When
        ResponseDTO responseDTO = userMapper.toPreviewUser(user);

        // Then
        assertNotNull(responseDTO);
        assertEquals(userId, responseDTO.getId());
        assertEquals("John Doe", responseDTO.getName());
        assertEquals("john@example.com", responseDTO.getEmail());
        assertEquals("hashedPassword", responseDTO.getPassword());
    }

    @Test
    void testToPreviewUser_WithNullValues_ShouldReturnResponseDTOWithNullValues() {
        // Given
        User user = new User(null, null, null, null);

        // When
        ResponseDTO responseDTO = userMapper.toPreviewUser(user);

        // Then
        assertNotNull(responseDTO);
        assertNull(responseDTO.getId());
        assertNull(responseDTO.getName());
        assertNull(responseDTO.getEmail());
        assertNull(responseDTO.getPassword());
    }

    @Test
    void testToPreviewUser_WithEmptyStrings_ShouldReturnResponseDTOWithEmptyStrings() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "", "", "");

        // When
        ResponseDTO responseDTO = userMapper.toPreviewUser(user);

        // Then
        assertNotNull(responseDTO);
        assertEquals(userId, responseDTO.getId());
        assertEquals("", responseDTO.getName());
        assertEquals("", responseDTO.getEmail());
        assertEquals("", responseDTO.getPassword());
    }

    @Test
    void testToUpdateEntity_PreservesUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        User existingUser = new User(userId, "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO("New Name", "new@example.com", "newPassword");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encryptedNew";
            mockedEncryptor.when(() -> encrypt("newPassword")).thenReturn(encryptedPassword);

            // When
            userMapper.toUpdateEntity(existingUser, dto);

            // Then
            assertEquals(userId, existingUser.getId()); // ID should remain unchanged
            assertEquals("New Name", existingUser.getName());
            assertEquals("new@example.com", existingUser.getEmail());
            assertEquals(encryptedPassword, existingUser.getPassword());

            mockedEncryptor.verify(() -> encrypt("newPassword"));
        }
    }

    @Test
    void testToEntity_WithValidFieldsAndSpaces_ShouldNotTrimValues() {
        // Given
        RequestDTO dto = new RequestDTO(" John Doe ", " john@example.com ", " Password123! ");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encrypted";
            mockedEncryptor.when(() -> encrypt(" Password123! ")).thenReturn(encryptedPassword);

            // When
            User user = userMapper.toEntity(dto);

            // Then
            assertNotNull(user);
            assertEquals(" John Doe ", user.getName()); // Should not be trimmed in the entity
            assertEquals(" john@example.com ", user.getEmail()); // Should not be trimmed in the entity
            assertEquals(encryptedPassword, user.getPassword());
        }
    }

    @Test
    void testToUpdateEntity_WithValidFieldsAndSpaces_ShouldUpdateWithSpaces() {
        // Given
        User existingUser = new User(UUID.randomUUID(), "Old Name", "old@example.com", "oldPassword");
        RequestDTO dto = new RequestDTO(" New Name ", " new@example.com ", " newPassword ");

        try (MockedStatic<com.example.test.config.PasswordEncryptor> mockedEncryptor = Mockito.mockStatic(com.example.test.config.PasswordEncryptor.class)) {
            String encryptedPassword = "$2a$10$encryptedWithSpaces";
            mockedEncryptor.when(() -> encrypt(" newPassword ")).thenReturn(encryptedPassword);

            // When
            userMapper.toUpdateEntity(existingUser, dto);

            // Then
            assertEquals(" New Name ", existingUser.getName()); // Should not be trimmed
            assertEquals(" new@example.com ", existingUser.getEmail()); // Should not be trimmed
            assertEquals(encryptedPassword, existingUser.getPassword());

            mockedEncryptor.verify(() -> encrypt(" newPassword "));
        }
    }
}