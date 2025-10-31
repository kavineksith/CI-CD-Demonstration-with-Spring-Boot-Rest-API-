package com.example.test.services;

import com.example.test.dto.RequestDTO;
import com.example.test.dto.ResponseDTO;
import com.example.test.exceptions.DuplicateUserException;
import com.example.test.exceptions.UserNotFoundException;
import com.example.test.model.User;
import com.example.test.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException("User with email " + email + " not found")
                );
    }

    public void userDoesExist(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateUserException("User with email " + email + " already exists");
        }
    }

    // To Save User
    public User toSaveUser(RequestDTO dto) {
        userDoesExist(dto.getEmail());

        return userRepository.save(userMapper.toEntity(dto));
    }

    // To Update User
    public User toUpdateUser(String email, RequestDTO dto) {
        User userToUpdate = findUserByEmail(email);

        userMapper.toUpdateEntity(userToUpdate, dto);
        return userRepository.save(userToUpdate);
    }

    // To Delete User
    public void toDeleteUser(String email) {
        findUserByEmail(email);
        userRepository.deleteByEmail(email);
    }

    // To Preview Single User
    @Transactional(readOnly = true)
    public ResponseDTO toPreviewUser(String email) {
        User toPreview = findUserByEmail(email);
        return userMapper.toPreviewUser(toPreview);
    }

    // To Preview All Users
    @Transactional(readOnly = true)
    public List<ResponseDTO> toPreviewAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toPreviewUser)
                .toList();
    }
}
