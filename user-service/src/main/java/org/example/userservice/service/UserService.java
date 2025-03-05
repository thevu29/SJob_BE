package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.UserCreationRequest;
import org.example.userservice.dto.request.UserUpdateRequest;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.entity.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        return users.stream().map(userMapper::toDto).toList();
    }

    public UserDTO getUser(String id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDTO createUser(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDTO updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userMapper.updateEntityFromRequest(request, user);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO blockUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(false);
        User blockedUser = userRepository.save(user);
        return userMapper.toDto(blockedUser);
    }

    public UserDTO unblockUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(true);
        User unblockedUser = userRepository.save(user);
        return userMapper.toDto(unblockedUser);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }
}
