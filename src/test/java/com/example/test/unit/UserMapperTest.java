package com.example.test.unit;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.model.User;
import com.example.test.services.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.example.test.config.PasswordEncryptor.encrypt;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    @DisplayName("Should map RequestDTO to User entity")
    void shouldMapRequestDtoToEntity() {
        RequestDTO dto = new RequestDTO(
                "John Doe",
                "john.doe@example.com",
                "Password123!"
        );

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertNotEquals("Password123!", user.getPassword()); // Should be encrypted
    }

    @Test
    @DisplayName("Should throw exception when mapping empty fields")
    void shouldThrowExceptionForEmptyFields() {
        RequestDTO dto = new RequestDTO("", "", "");

        assertThrows(IllegalArgumentException.class, () -> {
            userMapper.toEntity(dto);
        });
    }

    @Test
    @DisplayName("Should update user entity from RequestDTO")
    void shouldUpdateEntity() {
        User user = new User(
                UUID.randomUUID(),
                "Old Name",
                "old.email@example.com",
                "oldPassword"
        );

        RequestDTO dto = new RequestDTO(
                "New Name",
                "new.email@example.com",
                "NewPassword123!"
        );

        userMapper.toUpdateEntity(user, dto);

        assertEquals("New Name", user.getName());
        assertEquals("new.email@example.com", user.getEmail());

        // Verify password is encrypted (not plain text)
        assertNotEquals("NewPassword123!", user.getPassword());
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$")); // BCrypt hash format
    }

    @Test
    @DisplayName("Should map User to ResponseDTO")
    void shouldMapUserToResponseDto() {
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "John Doe",
                "john.doe@example.com",
                "encryptedPassword"
        );

        ResponseDTO dto = userMapper.toPreviewUser(user);

        assertNotNull(dto);
        assertEquals(userId, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("john.doe@example.com", dto.getEmail());
    }
}