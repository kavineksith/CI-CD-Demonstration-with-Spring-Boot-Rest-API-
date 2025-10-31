package com.example.test.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    private static Validator validator;

    // Concrete implementation for testing the abstract class
    private static class TestUserDTO extends UserDTO {
        public TestUserDTO() {
            super();
        }

        public TestUserDTO(String name, String email, String password) {
            super(name, email, password);
        }
    }

    @BeforeAll  // ✅ Changed from @BeforeEach to @BeforeAll for better performance
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDefaultConstructor() {
        // When
        TestUserDTO userDTO = new TestUserDTO();

        // Then
        assertNull(userDTO.getName());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getPassword());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String name = "John Doe";
        String email = "john@example.com";
        String password = "Password123!";

        // When
        TestUserDTO userDTO = new TestUserDTO(name, email, password);

        // Then
        assertEquals(name, userDTO.getName());
        assertEquals(email, userDTO.getEmail());
        assertEquals(password, userDTO.getPassword());
    }

    @Test
    void testValidUserDTO_ShouldPassValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertTrue(violations.isEmpty(), "Expected no violations but got: " + violations);
    }

    @Test
    void testNullName_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO(null, "john@example.com", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertTrue(violations.size() >= 1, "Expected at least 1 violation");

        // Check that one of the violations is about name being required
        boolean hasNameRequiredViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Name is required"));
        assertTrue(hasNameRequiredViolation, "Expected 'Name is required' violation");
    }

    @Test
    void testBlankName_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("   ", "john@example.com", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Name cannot be blank", violation.getMessage());
    }

    @Test
    void testShortName_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("A", "john@example.com", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Name must be between 2 and 50 characters", violation.getMessage());
    }

    @Test
    void testLongName_ShouldFailValidation() {
        // Given
        String longName = "A".repeat(51);
        TestUserDTO userDTO = new TestUserDTO(longName, "john@example.com", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Name must be between 2 and 50 characters", violation.getMessage());
    }

    @Test
    void testNullEmail_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", null, "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertTrue(violations.size() >= 1, "Expected at least 1 violation");

        // Check that one of the violations is about email being required
        boolean hasEmailRequiredViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email is required"));
        assertTrue(hasEmailRequiredViolation, "Expected 'Email is required' violation");
    }

    @Test
    void testBlankEmail_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "   ", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        // Should have at least @NotBlank violation, might have @Email too
        assertTrue(violations.size() >= 1);
    }

    @Test
    void testInvalidEmail_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "invalid-email", "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Please provide a valid email address", violation.getMessage());
    }

    @Test
    void testLongEmail_ShouldFailValidation() {
        // Given
        String longEmail = "a".repeat(90) + "@example.com"; // Over 100 characters
        TestUserDTO userDTO = new TestUserDTO("John Doe", longEmail, "Password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertTrue(violations.size() >= 1, "Expected at least 1 violation");

        // Check that one of the violations is about email size
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Email cannot exceed 100 characters"));
        assertTrue(hasSizeViolation, "Expected 'Email cannot exceed 100 characters' violation");
    }

    @Test
    void testNullPassword_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", null);

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertTrue(violations.size() >= 1, "Expected at least 1 violation");

        // Check that one of the violations is about password being required
        boolean hasPasswordRequiredViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password is required"));
        assertTrue(hasPasswordRequiredViolation, "Expected 'Password is required' violation");
    }

    @Test
    void testBlankPassword_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "   ");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        // Should have at least @NotBlank, might also have @Pattern and @Size
        assertTrue(violations.size() >= 1);
    }

    @Test
    void testShortPassword_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "Pass1!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertTrue(violations.size() >= 1, "Expected at least 1 violation");

        // Check that one of the violations is about password size
        boolean hasSizeViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Password must be between 8 and 255 characters"));
        assertTrue(hasSizeViolation, "Expected 'Password must be between 8 and 255 characters' violation");
    }

    @Test
    void testLongPassword_ShouldFailValidation() {
        // Given
        String longPassword = "Password123!".repeat(25); // Over 255 characters
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", longPassword);

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Password must be between 8 and 255 characters", violation.getMessage());
    }

    @Test
    void testPasswordWithoutUppercase_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "password123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character", violation.getMessage());
    }

    @Test
    void testPasswordWithoutLowercase_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "PASSWORD123!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character", violation.getMessage());
    }

    @Test
    void testPasswordWithoutDigit_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "Password!");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character", violation.getMessage());
    }

    @Test
    void testPasswordWithoutSpecialCharacter_ShouldFailValidation() {
        // Given
        TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", "Password123");

        // When
        Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);

        // Then
        assertFalse(violations.isEmpty(), "Expected violations but got none");
        assertEquals(1, violations.size());
        ConstraintViolation<TestUserDTO> violation = violations.iterator().next();
        assertEquals("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character", violation.getMessage());
    }

    @Test
    void testSetters() {
        // Given
        TestUserDTO userDTO = new TestUserDTO();

        // When
        userDTO.setName("Jane Doe");
        userDTO.setEmail("jane@example.com");
        userDTO.setPassword("NewPassword123!");

        // Then
        assertEquals("Jane Doe", userDTO.getName());
        assertEquals("jane@example.com", userDTO.getEmail());
        assertEquals("NewPassword123!", userDTO.getPassword());
    }

    @Test
    void testValidEmailFormats() {
        String[] validEmails = {
                "test@example.com",
                "user.name@domain.co.uk",
                "user+tag@example.org",
                "123@domain.com"
        };

        for (String email : validEmails) {
            TestUserDTO userDTO = new TestUserDTO("John Doe", email, "Password123!");
            Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);
            assertTrue(violations.isEmpty(), "Email " + email + " should be valid but got violations: " + violations);
        }
    }

    @Test
    void testValidPasswordFormats() {
        String[] validPasswords = {
                "Password123!",
                "Myp@ssw0rd",
                "Str0ng!Pass",
                "C0mplex&Password"
        };

        for (String password : validPasswords) {
            TestUserDTO userDTO = new TestUserDTO("John Doe", "john@example.com", password);
            Set<ConstraintViolation<TestUserDTO>> violations = validator.validate(userDTO);
            assertTrue(violations.isEmpty(), "Password " + password + " should be valid but got violations: " + violations);
        }
    }

    @Test
    void testBoundaryValues() {
        // Test minimum valid name length
        TestUserDTO userDTO1 = new TestUserDTO("Jo", "john@example.com", "Password123!");
        Set<ConstraintViolation<TestUserDTO>> violations1 = validator.validate(userDTO1);
        assertTrue(violations1.isEmpty(), "Minimum name length should be valid");

        // Test maximum valid name length
        String maxName = "A".repeat(50);
        TestUserDTO userDTO2 = new TestUserDTO(maxName, "john@example.com", "Password123!");
        Set<ConstraintViolation<TestUserDTO>> violations2 = validator.validate(userDTO2);
        assertTrue(violations2.isEmpty(), "Maximum name length should be valid");

        // Test minimum valid password length
        TestUserDTO userDTO3 = new TestUserDTO("John Doe", "john@example.com", "Pass123!");
        Set<ConstraintViolation<TestUserDTO>> violations3 = validator.validate(userDTO3);
        assertTrue(violations3.isEmpty(), "Minimum password length should be valid");
    }
}