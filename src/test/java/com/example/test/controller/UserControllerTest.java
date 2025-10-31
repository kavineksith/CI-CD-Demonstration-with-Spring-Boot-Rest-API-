package com.example.test.controller;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.exceptions.GlobalExceptionHandler;
import com.example.test.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // ✅ IMPORTANT: Add GlobalExceptionHandler to handle exceptions properly
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testToCreateUser_WithValidData_ShouldReturn201() throws Exception {
        // Given
        RequestDTO dto = new RequestDTO("John Doe", "john@example.com", "Password123!");

        // When & Then
        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        verify(userService).toSaveUser(any(RequestDTO.class));
    }

    @Test
    void testToUpdateUser_WithValidEmailAndData_ShouldReturn200() throws Exception {
        // Given
        String email = "john@example.com";
        RequestDTO dto = new RequestDTO("John Updated", "john@example.com", "NewPassword123!");

        // When & Then
        mockMvc.perform(put("/users/update")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).toUpdateUser(eq(email), any(RequestDTO.class));
    }

    @Test
    void testToDeleteUser_WithValidEmail_ShouldReturn204() throws Exception {
        // Given
        String email = "john@example.com";

        // When & Then
        mockMvc.perform(delete("/users/delete")
                        .param("email", email))
                .andExpect(status().isNoContent());

        verify(userService).toDeleteUser(email);
    }

    @Test
    void testToDeleteUser_WithNullEmail_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(delete("/users/delete"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).toDeleteUser(anyString());
    }

    @Test
    void testToDeleteUser_WithEmptyEmail_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(delete("/users/delete")
                        .param("email", ""))  // ✅ Empty string
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(userService, never()).toDeleteUser(anyString());
    }

    @Test
    void testToDeleteUser_WithInvalidEmailFormat_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(delete("/users/delete")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid email format"));

        verify(userService, never()).toDeleteUser(anyString());
    }

    @Test
    void testToPreviewUser_WithValidEmail_ShouldReturnUser() throws Exception {
        // Given
        String email = "john@example.com";
        ResponseDTO responseDTO = new ResponseDTO(UUID.randomUUID(), "John Doe", email, "hashedPassword");
        when(userService.toPreviewUser(email)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/users/preview")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value(email));

        verify(userService).toPreviewUser(email);
    }

    @Test
    void testToPreviewUser_WithNullEmail_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/preview"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).toPreviewUser(anyString());
    }

    @Test
    void testToPreviewUser_WithEmptyEmail_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/preview")
                        .param("email", ""))  // ✅ Empty string
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(userService, never()).toPreviewUser(anyString());
    }

    @Test
    void testToPreviewUser_WithInvalidEmailFormat_ShouldThrowIllegalArgumentException() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/preview")
                        .param("email", "invalid-email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid email format"));

        verify(userService, never()).toPreviewUser(anyString());
    }

    @Test
    void testToPreviewAllUsers_WithUsers_ShouldReturnUsers() throws Exception {
        // Given
        List<ResponseDTO> users = Arrays.asList(
                new ResponseDTO(UUID.randomUUID(), "John Doe", "john@example.com", "hashedPassword1"),
                new ResponseDTO(UUID.randomUUID(), "Jane Smith", "jane@example.com", "hashedPassword2")
        );
        when(userService.toPreviewAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));

        verify(userService).toPreviewAllUsers();
    }

    @Test
    void testToPreviewAllUsers_WithEmptyList_ShouldReturnNoContent() throws Exception {
        // Given
        when(userService.toPreviewAllUsers()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/users/all"))
                .andExpect(status().isNoContent());

        verify(userService).toPreviewAllUsers();
    }

    @Test
    void testValidateEmailParameter_WithValidEmail_ShouldPass() throws Exception {
        // Given
        String validEmail = "test@example.com";
        ResponseDTO responseDTO = new ResponseDTO(UUID.randomUUID(), "Test", validEmail, "pass");
        when(userService.toPreviewUser(validEmail)).thenReturn(responseDTO);

        // When & Then
        mockMvc.perform(get("/users/preview")
                        .param("email", validEmail))
                .andExpect(status().isOk());

        verify(userService).toPreviewUser(validEmail);
    }

    @Test
    void testValidateEmailParameter_WithWhitespaceOnlyEmail_ShouldThrowException() throws Exception {
        // When & Then
        mockMvc.perform(get("/users/preview")
                        .param("email", "   "))  // ✅ Whitespace only
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Email parameter is required and cannot be empty"));

        verify(userService, never()).toPreviewUser(anyString());
    }

    @Test
    void testIsValidEmailFormat_WithValidFormats() throws Exception {
        // Test various valid email formats
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@domain.com",
                "a@b.co"
        };

        for (String email : validEmails) {
            ResponseDTO responseDTO = new ResponseDTO(UUID.randomUUID(), "Test", email, "pass");
            when(userService.toPreviewUser(email)).thenReturn(responseDTO);

            mockMvc.perform(get("/users/preview")
                            .param("email", email))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void testIsValidEmailFormat_WithInvalidFormats() throws Exception {
        // Test various invalid email formats
        String[] invalidEmails = {
                "invalid-email",        // No @ symbol
                "@domain.com",          // Missing local part (starts with @)
                "user@",                // Missing domain
                "user@domain",          // Missing TLD
                "user.domain.com"       // No @ symbol
        };

        for (String email : invalidEmails) {
            mockMvc.perform(get("/users/preview")
                            .param("email", email))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Invalid email format"));
        }

        verify(userService, never()).toPreviewUser(anyString());
    }
}