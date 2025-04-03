package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.UserDTO;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.entity.User;
import org.example.userservice.entity.UserRole;
import org.example.userservice.exception.ResourceNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private String escapeRegexSpecialChars(String input) {
        if (input == null) return "";
        return input.replaceAll("[-\\[\\]{}()*+?.,\\\\^$|#\\s]", "\\\\$0");
    }

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAllByDeletedAtIsNull();
        return users.stream().map(userMapper::toDto).toList();
    }

    public List<UserDTO> findUsers(String email, String role, Boolean active) {
        String sanitizedEmail = escapeRegexSpecialChars(email);
        UserRole userRole = UserRole.valueOf(role);

        boolean filterByActive = active != null;
        boolean isActive = Boolean.TRUE.equals(active);

        List<User> users = userRepository.findUsers(sanitizedEmail, userRole, isActive, filterByActive);
        return users.stream().map(userMapper::toDto).toList();
    }

    public Page<UserDTO> findPagedAdmins(
            String emailPattern,
            Boolean active,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        String sanitizedPattern = escapeRegexSpecialChars(emailPattern);

        boolean filterByStatus = active != null;
        boolean isActive = Boolean.TRUE.equals(active);

        Page<User> userPage =  userRepository.findAdmins(sanitizedPattern, isActive, filterByStatus, pageable);

        return userPage.map(userMapper::toDto);
    }

    public List<UserDTO> getUsersByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return userRepository.findByIdInAndDeletedAtIsNull(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDTO updateUserStatus(String id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(active);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void softDeleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }
}
