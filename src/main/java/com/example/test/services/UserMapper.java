package com.example.test.services;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.model.User;
import org.springframework.stereotype.Component;

import static com.example.test.config.PasswordEncryptor.encrypt;

@Component
public class UserMapper {
    public User toEntity(RequestDTO dto) {
        if (dto.getName().trim().isEmpty() || dto.getEmail().trim().isEmpty() || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("All fields are required");
        }

        return new User(
                dto.getName(),
                dto.getEmail(),
                encrypt(dto.getPassword())
        );
    }

    public void toUpdateEntity(User user, RequestDTO dto) {
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(encrypt(dto.getPassword()));  // ✅ Fixed: encrypt password on update
        }
    }

    public ResponseDTO toPreviewUser(User user) {
        return new ResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }
}