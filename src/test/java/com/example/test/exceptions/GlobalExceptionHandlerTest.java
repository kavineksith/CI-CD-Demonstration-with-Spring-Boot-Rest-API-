package com.example.test.exceptions;

import com.example.test.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private MissingServletRequestParameterException missingServletRequestParameterException;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private final String testUri = "/test/uri";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(testUri);
    }

    @Test
    void testHandleUserNotFoundException() {
        // Given
        String message = "User not found";
        UserNotFoundException exception = new UserNotFoundException(message);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleUserNotFoundException(exception, request);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleDuplicateUserException() {
        // Given
        String message = "User already exists";
        DuplicateUserException exception = new DuplicateUserException(message);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleDuplicateUserException(exception, request);

        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(409, errorResponse.getStatus());
        assertEquals("Conflict", errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleValidationException() {
        // Given
        FieldError fieldError1 = new FieldError("user", "name", "Name is required");
        FieldError fieldError2 = new FieldError("user", "email", "Email is invalid");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Invalid input data", errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());

        List<String> details = errorResponse.getDetails();
        assertNotNull(details);
        assertEquals(2, details.size());
        assertTrue(details.contains("name: Name is required"));
        assertTrue(details.contains("email: Email is invalid"));
    }

    @Test
    void testHandleValidationException_WithNoFieldErrors() {
        // Given
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Invalid input data", errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());

        List<String> details = errorResponse.getDetails();
        assertNotNull(details);
        assertTrue(details.isEmpty());
    }

    @Test
    void testHandleIllegalArgumentException() {
        // Given
        String message = "Email parameter is required";
        IllegalArgumentException exception = new IllegalArgumentException(message);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleIllegalArgumentException(exception, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleMissingParameterException() {
        // Given
        String parameterName = "email";
        when(missingServletRequestParameterException.getParameterName()).thenReturn(parameterName);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleMissingParameterException(missingServletRequestParameterException, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(400, errorResponse.getStatus());
        assertEquals("Bad Request", errorResponse.getError());
        assertEquals("Missing required parameter: " + parameterName, errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleRuntimeException() {
        // Given
        String message = "Database connection failed";
        RuntimeException exception = new RuntimeException(message);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleRuntimeException(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred: " + message, errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleRuntimeException_WithNullMessage() {
        // Given
        RuntimeException exception = new RuntimeException();

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleRuntimeException(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred: null", errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
    }

    @Test
    void testHandleGenericException() {
        // Given
        String message = "Unexpected error occurred";
        Exception exception = new Exception(message);

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGenericException(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
        assertEquals(testUri, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testHandleGenericException_WithNullMessage() {
        // Given
        Exception exception = new Exception();

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleGenericException(exception, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("An unexpected error occurred", errorResponse.getMessage());
    }

    @Test
    void testAllHandlersSetCorrectTimestamp() {
        // Test that all handlers set timestamps correctly by ensuring they're recent

        // UserNotFoundException
        UserNotFoundException userNotFound = new UserNotFoundException("User not found");
        ResponseEntity<ErrorResponseDTO> response1 = globalExceptionHandler.handleUserNotFoundException(userNotFound, request);
        assertNotNull(response1.getBody().getTimestamp());

        // DuplicateUserException
        DuplicateUserException duplicateUser = new DuplicateUserException("Duplicate user");
        ResponseEntity<ErrorResponseDTO> response2 = globalExceptionHandler.handleDuplicateUserException(duplicateUser, request);
        assertNotNull(response2.getBody().getTimestamp());

        // IllegalArgumentException
        IllegalArgumentException illegalArg = new IllegalArgumentException("Invalid argument");
        ResponseEntity<ErrorResponseDTO> response3 = globalExceptionHandler.handleIllegalArgumentException(illegalArg, request);
        assertNotNull(response3.getBody().getTimestamp());

        // RuntimeException
        RuntimeException runtime = new RuntimeException("Runtime error");
        ResponseEntity<ErrorResponseDTO> response4 = globalExceptionHandler.handleRuntimeException(runtime, request);
        assertNotNull(response4.getBody().getTimestamp());

        // Generic Exception
        Exception generic = new Exception("Generic error");
        ResponseEntity<ErrorResponseDTO> response5 = globalExceptionHandler.handleGenericException(generic, request);
        assertNotNull(response5.getBody().getTimestamp());
    }

    @Test
    void testHandleValidationException_WithSingleFieldError() {
        // Given
        FieldError fieldError = new FieldError("user", "password", "Password is too weak");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        // When
        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleValidationException(methodArgumentNotValidException, request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponse = response.getBody();
        assertNotNull(errorResponse);

        List<String> details = errorResponse.getDetails();
        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals("password: Password is too weak", details.get(0));
    }
}