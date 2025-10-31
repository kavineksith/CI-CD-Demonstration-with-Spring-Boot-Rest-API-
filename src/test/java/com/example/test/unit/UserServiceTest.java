package com.example.test.unit;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.exceptions.DuplicateUserException;
import com.example.test.exceptions.UserNotFoundException;
import com.example.test.model.User;
import com.example.test.repository.UserRepository;
import com.example.test.services.UserMapper;
import com.example.test.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RequestDTO testRequestDTO;
    private ResponseDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        testUser = new User(
                UUID.randomUUID(),
                "John Doe",
                "john.doe@example.com",
                "encryptedPassword"
        );

        testRequestDTO = new RequestDTO(
                "John Doe",
                "john.doe@example.com",
                "Password123!"
        );

        testResponseDTO = new ResponseDTO(
                testUser.getId(),
                testUser.getName(),
                testUser.getEmail(),
                testUser.getPassword()
        );
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        User result = userService.findUserByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowUserNotFoundException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.findUserByEmail("nonexistent@example.com");
        });

        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw DuplicateUserException when user exists")
    void shouldThrowDuplicateUserException() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        assertThrows(DuplicateUserException.class, () -> {
            userService.userDoesExist(testUser.getEmail());
        });

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        when(userRepository.findByEmail(testRequestDTO.getEmail()))
                .thenReturn(Optional.empty());
        when(userMapper.toEntity(testRequestDTO))
                .thenReturn(testUser);
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User result = userService.toSaveUser(testRequestDTO);

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(testRequestDTO.getEmail());
        verify(userMapper, times(1)).toEntity(testRequestDTO);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).toUpdateEntity(testUser, testRequestDTO);
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);

        User result = userService.toUpdateUser(testUser.getEmail(), testRequestDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userMapper, times(1)).toUpdateEntity(testUser, testRequestDTO);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteByEmail(testUser.getEmail());

        userService.toDeleteUser(testUser.getEmail());

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userRepository, times(1)).deleteByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should preview user successfully")
    void shouldPreviewUser() {
        when(userRepository.findByEmail(testUser.getEmail()))
                .thenReturn(Optional.of(testUser));
        when(userMapper.toPreviewUser(testUser))
                .thenReturn(testResponseDTO);

        ResponseDTO result = userService.toPreviewUser(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userMapper, times(1)).toPreviewUser(testUser);
    }

    @Test
    @DisplayName("Should preview all users successfully")
    void shouldPreviewAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toPreviewUser(any(User.class)))
                .thenReturn(testResponseDTO);

        List<ResponseDTO> result = userService.toPreviewAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toPreviewUser(testUser);
    }
}