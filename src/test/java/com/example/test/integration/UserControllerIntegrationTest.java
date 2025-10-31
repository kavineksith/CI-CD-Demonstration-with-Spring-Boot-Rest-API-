package com.example.test.integration;

import com.example.test.dto.RequestDTO;
import com.example.test.model.User;
import com.example.test.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // ✅ Better lifecycle management
class UserControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        // ✅ Clean database before each test
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // ✅ Clean up after each test
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        RequestDTO requestDTO = new RequestDTO(
                "John Doe",
                "john.doe@example.com",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201);

        // Verify user was created in database
        User user = userRepository.findByEmail(requestDTO.getEmail()).orElse(null);
        Assertions.assertNotNull(user);
        Assertions.assertEquals("John Doe", user.getName());
    }

    @Test
    @Order(2)
    @DisplayName("Should fail to create duplicate user")
    void shouldFailToCreateDuplicateUser() {
        RequestDTO requestDTO = new RequestDTO(
                "John Doe",
                "duplicate@example.com",
                "Password123!"
        );

        // Create first user
        given()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201);

        // Try to create duplicate
        given()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(409)
                .body("status", equalTo(409))
                .body("error", equalTo("Conflict"))
                .body("message", containsString("already exists"));
    }

    @Test
    @Order(3)
    @DisplayName("Should fail validation when creating user with invalid data")
    void shouldFailValidationOnCreate() {
        RequestDTO requestDTO = new RequestDTO(
                "J", // Too short
                "invalid-email", // Invalid email format
                "weak" // Weak password
        );

        given()
                .contentType(ContentType.JSON)
                .body(requestDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Validation Failed"))
                .body("details", hasSize(greaterThan(0)));
    }

    @Test
    @Order(4)
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        // Create user first
        RequestDTO createDTO = new RequestDTO(
                "Jane Doe",
                "jane.doe@example.com",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(createDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201);

        // Update user
        RequestDTO updateDTO = new RequestDTO(
                "Jane Smith",
                "jane.smith@example.com",
                "NewPassword123!"
        );

        given()
                .contentType(ContentType.JSON)
                .queryParam("email", createDTO.getEmail())  // ✅ Use queryParam
                .body(updateDTO)
                .when()
                .put("/users/update")
                .then()
                .statusCode(200);

        // Verify update - check by new email
        User updatedUser = userRepository.findByEmail("jane.smith@example.com").orElse(null);
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("Jane Smith", updatedUser.getName());
    }

    @Test
    @Order(5)
    @DisplayName("Should fail to update non-existent user")
    void shouldFailToUpdateNonExistentUser() {
        RequestDTO updateDTO = new RequestDTO(
                "Non Existent",
                "nonexistent@example.com",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .queryParam("email", "nonexistent@example.com")  // ✅ Use queryParam
                .body(updateDTO)
                .when()
                .put("/users/update")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(6)
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        // Create user first
        RequestDTO createDTO = new RequestDTO(
                "Delete Me",
                "delete.me@example.com",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(createDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201);

        // Delete user
        given()
                .queryParam("email", createDTO.getEmail())  // ✅ Use queryParam
                .when()
                .delete("/users/delete")
                .then()
                .statusCode(204);

        // Verify deletion
        Assertions.assertTrue(userRepository.findByEmail(createDTO.getEmail()).isEmpty());
    }

    @Test
    @Order(7)
    @DisplayName("Should fail to delete non-existent user")
    void shouldFailToDeleteNonExistentUser() {
        given()
                .queryParam("email", "nonexistent@example.com")  // ✅ Use queryParam
                .when()
                .delete("/users/delete")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(8)
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() {
        // Create user first
        RequestDTO createDTO = new RequestDTO(
                "Preview Me",
                "preview.me@example.com",
                "Password123!"
        );

        given()
                .contentType(ContentType.JSON)
                .body(createDTO)
                .when()
                .post("/users/create")
                .then()
                .statusCode(201);

        // Get user
        given()
                .queryParam("email", createDTO.getEmail())  // ✅ Use queryParam
                .when()
                .get("/users/preview")
                .then()
                .statusCode(200)
                .body("name", equalTo("Preview Me"))
                .body("email", equalTo("preview.me@example.com"))
                .body("id", notNullValue());
    }

    @Test
    @Order(9)
    @DisplayName("Should fail to get non-existent user")
    void shouldFailToGetNonExistentUser() {
        given()
                .queryParam("email", "nonexistent@example.com")  // ✅ Use queryParam
                .when()
                .get("/users/preview")
                .then()
                .statusCode(404)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"));
    }

    @Test
    @Order(10)
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        // Create multiple users
        RequestDTO user1 = new RequestDTO("User One", "user1@example.com", "Password123!");
        RequestDTO user2 = new RequestDTO("User Two", "user2@example.com", "Password123!");

        given().contentType(ContentType.JSON).body(user1).post("/users/create");
        given().contentType(ContentType.JSON).body(user2).post("/users/create");

        given()
                .when()
                .get("/users/all")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("[0].name", notNullValue())
                .body("[1].name", notNullValue());
    }

    @Test
    @Order(11)
    @DisplayName("Should return 204 when no users exist")
    void shouldReturn204WhenNoUsersExist() {
        given()
                .when()
                .get("/users/all")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(12)
    @DisplayName("Should fail with invalid email format")
    void shouldFailWithInvalidEmailFormat() {
        given()
                .queryParam("email", "invalid-email-format")  // ✅ Use queryParam
                .when()
                .get("/users/preview")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", containsString("Invalid email format"));
    }

    @Test
    @Order(13)
    @DisplayName("Should fail with empty email parameter")
    void shouldFailWithEmptyEmailParameter() {
        given()
                .queryParam("email", "")  // ✅ Use queryParam
                .when()
                .get("/users/preview")
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"));
    }

    @Test
    @Order(14)
    @DisplayName("Should fail with missing email parameter")
    void shouldFailWithMissingEmailParameter() {
        given()
                .when()
                .get("/users/preview")  // ✅ No email parameter
                .then()
                .statusCode(400)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", containsString("Missing required parameter"));
    }
}