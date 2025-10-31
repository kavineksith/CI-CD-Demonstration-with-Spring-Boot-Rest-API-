package com.example.test.services;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.exceptions.DuplicateUserException;
import com.example.test.exceptions.UserNotFoundException;
import com.example.test.model.User;
import com.example.test.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RequestDTO testRequestDTO;
    private ResponseDTO testResponseDTO;
    private final String testEmail = "john@example.com";
    private final UUID testId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = new User(testId, "John Doe", testEmail, "hashedPassword");
        testRequestDTO = new RequestDTO("John Doe", testEmail, "Password123!");
        testResponseDTO = new ResponseDTO(testId, "John Doe", testEmail, "hashedPassword");
    }

    @Test
    void testFindUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findUserByEmail(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testFindUserByEmail_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.findUserByEmail(email)
        );

        assertEquals("User with email " + email + " not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testUserDoesExist_WhenUserExists_ShouldThrowDuplicateUserException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When & Then
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () ->
                userService.userDoesExist(testEmail)
        );

        assertEquals("User with email " + testEmail + " already exists", exception.getMessage());
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testUserDoesExist_WhenUserDoesNotExist_ShouldNotThrowException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> userService.userDoesExist(testEmail));
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void testToSaveUser_WhenUserDoesNotExist_ShouldSaveAndReturnUser() {
        // Given
        when(userRepository.findByEmail(testRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(testRequestDTO)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = userService.toSaveUser(testRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(testRequestDTO.getEmail());
        verify(userMapper).toEntity(testRequestDTO);
        verify(userRepository).save(testUser);
    }

    @Test
    void testToSaveUser_WhenUserAlreadyExists_ShouldThrowDuplicateUserException() {
        // Given
        when(userRepository.findByEmail(testRequestDTO.getEmail())).thenReturn(Optional.of(testUser));

        // When & Then
        DuplicateUserException exception = assertThrows(DuplicateUserException.class, () ->
                userService.toSaveUser(testRequestDTO)
        );

        assertEquals("User with email " + testRequestDTO.getEmail() + " already exists", exception.getMessage());
        verify(userRepository).findByEmail(testRequestDTO.getEmail());
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testToUpdateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        // Given
        User updatedUser = new User(testId, "Updated Name", testEmail, "newPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(updatedUser);

        // When
        User result = userService.toUpdateUser(testEmail, testRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(updatedUser, result);
        verify(userRepository).findByEmail(testEmail);
        verify(userMapper).toUpdateEntity(testUser, testRequestDTO);
        verify(userRepository).save(testUser);
    }

    @Test
    void testToUpdateUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.toUpdateUser(testEmail, testRequestDTO)
        );

        assertEquals("User with email " + testEmail + " not found", exception.getMessage());
        verify(userRepository).findByEmail(testEmail);
        verify(userMapper, never()).toUpdateEntity(any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testToDeleteUser_WhenUserExists_ShouldDeleteUser() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        userService.toDeleteUser(testEmail);

        // Then
        verify(userRepository).findByEmail(testEmail);
        verify(userRepository).deleteByEmail(testEmail);
    }

    @Test
    void testToDeleteUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.toDeleteUser(testEmail)
        );

        assertEquals("User with email " + testEmail + " not found", exception.getMessage());
        verify(userRepository).findByEmail(testEmail);
        verify(userRepository, never()).deleteByEmail(anyString());
    }

    @Test
    void testToPreviewUser_WhenUserExists_ShouldReturnResponseDTO() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userMapper.toPreviewUser(testUser)).thenReturn(testResponseDTO);

        // When
        ResponseDTO result = userService.toPreviewUser(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDTO, result);
        assertEquals(testEmail, result.getEmail());
        verify(userRepository).findByEmail(testEmail);
        verify(userMapper).toPreviewUser(testUser);
    }

    @Test
    void testToPreviewUser_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.toPreviewUser(testEmail)
        );

        assertEquals("User with email " + testEmail + " not found", exception.getMessage());
        verify(userRepository).findByEmail(testEmail);
        verify(userMapper, never()).toPreviewUser(any());
    }

    @Test
    void testToPreviewAllUsers_WhenUsersExist_ShouldReturnListOfResponseDTOs() {
        // Given
        User user1 = new User(UUID.randomUUID(), "John Doe", "john@example.com", "pass1");
        User user2 = new User(UUID.randomUUID(), "Jane Smith", "jane@example.com", "pass2");
        List<User> users = Arrays.asList(user1, user2);

        ResponseDTO response1 = new ResponseDTO(user1.getId(), "John Doe", "john@example.com", "pass1");
        ResponseDTO response2 = new ResponseDTO(user2.getId(), "Jane Smith", "jane@example.com", "pass2");

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toPreviewUser(user1)).thenReturn(response1);
        when(userMapper.toPreviewUser(user2)).thenReturn(response2);

        // When
        List<ResponseDTO> result = userService.toPreviewAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
        verify(userRepository).findAll();
        verify(userMapper).toPreviewUser(user1);
        verify(userMapper).toPreviewUser(user2);
    }

    @Test
    void testToPreviewAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<ResponseDTO> result = userService.toPreviewAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verify(userMapper, never()).toPreviewUser(any());
    }

    @Test
    void testToPreviewAllUsers_WithSingleUser_ShouldReturnSingleItemList() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toPreviewUser(testUser)).thenReturn(testResponseDTO);

        // When
        List<ResponseDTO> result = userService.toPreviewAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testResponseDTO, result.get(0));
        verify(userRepository).findAll();
        verify(userMapper).toPreviewUser(testUser);
    }

    @Test
    void testFindUserByEmail_WithNullEmail_ShouldCallRepository() {
        // Given
        String nullEmail = null;
        when(userRepository.findByEmail(nullEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.findUserByEmail(nullEmail)
        );

        assertEquals("User with email " + nullEmail + " not found", exception.getMessage());
        verify(userRepository).findByEmail(nullEmail);
    }

    @Test
    void testFindUserByEmail_WithEmptyEmail_ShouldCallRepository() {
        // Given
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userService.findUserByEmail(emptyEmail)
        );

        assertEquals("User with email " + emptyEmail + " not found", exception.getMessage());
        verify(userRepository).findByEmail(emptyEmail);
    }

    @Test
    void testUserDoesExist_WithNullEmail_ShouldNotThrowException() {
        // Given
        String nullEmail = null;
        when(userRepository.findByEmail(nullEmail)).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> userService.userDoesExist(nullEmail));
        verify(userRepository).findByEmail(nullEmail);
    }

    @Test
    void testToSaveUser_VerifyMethodCallOrder() {
        // Given
        when(userRepository.findByEmail(testRequestDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(testRequestDTO)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.toSaveUser(testRequestDTO);

        // Then
        var inOrder = inOrder(userRepository, userMapper, userRepository);
        inOrder.verify(userRepository).findByEmail(testRequestDTO.getEmail());
        inOrder.verify(userMapper).toEntity(testRequestDTO);
        inOrder.verify(userRepository).save(testUser);
    }

    @Test
    void testToUpdateUser_VerifyMethodCallOrder() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        userService.toUpdateUser(testEmail, testRequestDTO);

        // Then
        var inOrder = inOrder(userRepository, userMapper, userRepository);
        inOrder.verify(userRepository).findByEmail(testEmail);
        inOrder.verify(userMapper).toUpdateEntity(testUser, testRequestDTO);
        inOrder.verify(userRepository).save(testUser);
    }

    @Test
    void testToDeleteUser_VerifyMethodCallOrder() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        userService.toDeleteUser(testEmail);

        // Then
        var inOrder = inOrder(userRepository, userRepository);
        inOrder.verify(userRepository).findByEmail(testEmail);
        inOrder.verify(userRepository).deleteByEmail(testEmail);
    }

    @Test
    void testToPreviewUser_VerifyMethodCallOrder() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userMapper.toPreviewUser(testUser)).thenReturn(testResponseDTO);

        // When
        userService.toPreviewUser(testEmail);

        // Then
        var inOrder = inOrder(userRepository, userMapper);
        inOrder.verify(userRepository).findByEmail(testEmail);
        inOrder.verify(userMapper).toPreviewUser(testUser);
    }

    @Test
    void testToPreviewAllUsers_VerifyStreamProcessing() {
        // Given
        User user1 = new User(UUID.randomUUID(), "User1", "user1@example.com", "pass1");
        User user2 = new User(UUID.randomUUID(), "User2", "user2@example.com", "pass2");
        User user3 = new User(UUID.randomUUID(), "User3", "user3@example.com", "pass3");
        List<User> users = Arrays.asList(user1, user2, user3);

        ResponseDTO response1 = new ResponseDTO(user1.getId(), "User1", "user1@example.com", "pass1");
        ResponseDTO response2 = new ResponseDTO(user2.getId(), "User2", "user2@example.com", "pass2");
        ResponseDTO response3 = new ResponseDTO(user3.getId(), "User3", "user3@example.com", "pass3");

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toPreviewUser(user1)).thenReturn(response1);
        when(userMapper.toPreviewUser(user2)).thenReturn(response2);
        when(userMapper.toPreviewUser(user3)).thenReturn(response3);

        // When
        List<ResponseDTO> result = userService.toPreviewAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));
        assertEquals(response3, result.get(2));
        verify(userRepository).findAll();
        verify(userMapper, times(3)).toPreviewUser(any(User.class));
    }

    @Test
    void testService_AllMethodsHandleRepositoryExceptions() {
        // Test that service methods properly propagate repository exceptions

        // Given
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(userRepository.findByEmail(anyString())).thenThrow(repositoryException);

        // When & Then - findUserByEmail
        RuntimeException exception1 = assertThrows(RuntimeException.class, () ->
                userService.findUserByEmail(testEmail)
        );
        assertEquals("Database error", exception1.getMessage());

        // When & Then - userDoesExist
        RuntimeException exception2 = assertThrows(RuntimeException.class, () ->
                userService.userDoesExist(testEmail)
        );
        assertEquals("Database error", exception2.getMessage());

        // When & Then - toPreviewUser
        RuntimeException exception3 = assertThrows(RuntimeException.class, () ->
                userService.toPreviewUser(testEmail)
        );
        assertEquals("Database error", exception3.getMessage());
    }
}