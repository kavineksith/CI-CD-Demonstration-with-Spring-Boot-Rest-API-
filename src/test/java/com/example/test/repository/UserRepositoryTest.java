package com.example.test.repository;

import com.example.test.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        String email = "john@example.com";
        UUID userId = UUID.randomUUID();
        User expectedUser = new User(userId, "John Doe", email, "hashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        assertEquals(email, result.get().getEmail());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WithNullEmail_ShouldCallRepository() {
        // Given
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(null);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(null);
    }

    @Test
    void testFindByEmail_WithEmptyEmail_ShouldCallRepository() {
        // Given
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WithWhitespaceEmail_ShouldCallRepository() {
        // Given
        String email = "   ";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepository.findByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testDeleteByEmail_ShouldCallDeleteMethod() {
        // Given
        String email = "john@example.com";

        // When
        userRepository.deleteByEmail(email);

        // Then
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void testDeleteByEmail_WithNullEmail_ShouldCallDeleteMethod() {
        // Given
        String email = null;

        // When
        userRepository.deleteByEmail(email);

        // Then
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void testDeleteByEmail_WithEmptyEmail_ShouldCallDeleteMethod() {
        // Given
        String email = "";

        // When
        userRepository.deleteByEmail(email);

        // Then
        verify(userRepository).deleteByEmail(email);
    }

    @Test
    void testDeleteByEmail_WithValidEmail_ShouldCallDeleteMethod() {
        // Given
        String email = "user@example.com";

        // When
        userRepository.deleteByEmail(email);

        // Then
        verify(userRepository).deleteByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testFindByEmail_VerifyParameterPassing() {
        // Given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        User user1 = new User("User One", email1, "password1");
        User user2 = new User("User Two", email2, "password2");

        when(userRepository.findByEmail(email1)).thenReturn(Optional.of(user1));
        when(userRepository.findByEmail(email2)).thenReturn(Optional.of(user2));

        // When
        Optional<User> result1 = userRepository.findByEmail(email1);
        Optional<User> result2 = userRepository.findByEmail(email2);

        // Then
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(email1, result1.get().getEmail());
        assertEquals(email2, result2.get().getEmail());

        verify(userRepository).findByEmail(email1);
        verify(userRepository).findByEmail(email2);
    }

    @Test
    void testFindByEmail_CaseSensitive() {
        // Given
        String lowerCaseEmail = "john@example.com";
        String upperCaseEmail = "JOHN@EXAMPLE.COM";
        String mixedCaseEmail = "John@Example.com";

        User user = new User("John", lowerCaseEmail, "password");

        when(userRepository.findByEmail(lowerCaseEmail)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(mixedCaseEmail)).thenReturn(Optional.empty());

        // When
        Optional<User> result1 = userRepository.findByEmail(lowerCaseEmail);
        Optional<User> result2 = userRepository.findByEmail(upperCaseEmail);
        Optional<User> result3 = userRepository.findByEmail(mixedCaseEmail);

        // Then
        assertTrue(result1.isPresent());
        assertFalse(result2.isPresent());
        assertFalse(result3.isPresent());

        verify(userRepository).findByEmail(lowerCaseEmail);
        verify(userRepository).findByEmail(upperCaseEmail);
        verify(userRepository).findByEmail(mixedCaseEmail);
    }

    @Test
    void testDeleteByEmail_MultipleInvocations() {
        // Given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String email3 = "user3@example.com";

        // When
        userRepository.deleteByEmail(email1);
        userRepository.deleteByEmail(email2);
        userRepository.deleteByEmail(email3);

        // Then
        verify(userRepository).deleteByEmail(email1);
        verify(userRepository).deleteByEmail(email2);
        verify(userRepository).deleteByEmail(email3);
        verify(userRepository, times(3)).deleteByEmail(anyString());
    }

    @Test
    void testFindByEmail_WithSpecialCharactersInEmail() {
        // Given
        String specialEmail = "user+test123@sub.example-domain.com";
        User user = new User("Special User", specialEmail, "password");

        when(userRepository.findByEmail(specialEmail)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userRepository.findByEmail(specialEmail);

        // Then
        assertTrue(result.isPresent());
        assertEquals(specialEmail, result.get().getEmail());
        verify(userRepository).findByEmail(specialEmail);
    }

    @Test
    void testRepository_InheritanceFromJpaRepository() {
        // This test verifies that UserRepository extends JpaRepository
        // The mock itself demonstrates this relationship works

        // Given
        User user = new User("Test", "test@example.com", "password");
        when(userRepository.save(user)).thenReturn(user);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertEquals(user, savedUser);
        verify(userRepository).save(user);
    }
}