package com.example.test.controller;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> toCreateUser(@Valid @RequestBody RequestDTO dto) {
        userService.toSaveUser(dto);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/update")  // ✅ Fixed: removed ?email={email}
    public ResponseEntity<Void> toUpdateUser(@RequestParam String email, @Valid @RequestBody RequestDTO dto) {  // ✅ Fixed: moved @Valid
        userService.toUpdateUser(email, dto);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/delete")  // ✅ Fixed: removed ?email={email}
    public ResponseEntity<Void> toDeleteUser(@RequestParam String email) {
        validateEmailParameter(email);

        userService.toDeleteUser(email);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/preview")  // ✅ Fixed: removed ?email={email}
    public ResponseEntity<ResponseDTO> toPreviewUser(@RequestParam String email) {
        validateEmailParameter(email);

        ResponseDTO result = userService.toPreviewUser(email);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<java.util.List<ResponseDTO>> toPreviewAllUsers() {

        List<ResponseDTO> result = userService.toPreviewAllUsers();
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    private void validateEmailParameter(String email) {
        // Check if email is null or empty
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email parameter is required and cannot be empty");
        }

        // Check if email format is valid
        if (!isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
}