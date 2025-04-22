package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.common.dto.User.*;
import org.common.enums.UserRole;
import org.common.exception.ResourceNotFoundException;
import org.example.userservice.dto.UserUpdatePasswordDTO;
import org.example.userservice.dto.UserVerifyOtpDTO;
import org.example.userservice.entity.User;
import org.example.userservice.keycloak.KeycloakService;
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
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;

    private String escapeRegexSpecialChars(String input) {
        if (input == null) return "";
        return input.replaceAll("[-\\[\\]{}()*+?.,\\\\^$|#\\s]", "\\\\$0");
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).toList();
    }

    public Page<UserDTO> findPagedUsers(
            String query,
            Boolean active,
            String role,
            int page,
            int size,
            String sortBy,
            Sort.Direction direction
    ) {
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        String sanitizedQuery = escapeRegexSpecialChars(query);

        boolean filterByStatus = active != null;
        boolean isActive = Boolean.TRUE.equals(active);

        UserRole userRole = UserRole.valueOf(role);

        Page<User> userPage = userRepository.findPagedUsers(sanitizedQuery, isActive, filterByStatus, userRole, pageable);

        return userPage.map(userMapper::toDto);
    }

    public List<UserDTO> getUsersByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return userRepository.findByIdIn(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    public UserDTO createUser(UserCreationDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    public UserDTO updateUserOtp(UserUpdateOtpDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!request.getOtp().isBlank()) {
            user.setOtp(passwordEncoder.encode(request.getOtp()));
        }
        if (request.getOtpExpiresAt() != null) {
            user.setOtpExpiresAt(request.getOtpExpiresAt());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO verifyUserOtp(UserVerifyOtpDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (
                !passwordEncoder.matches(request.getOtp(), user.getOtp()) ||
                user.getOtpExpiresAt() == null ||
                user.getOtpExpiresAt().isBefore(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException("Invalid OTP or OTP expired");
        }

        user.setOtpVerified(true);
        user.setOtp(null);
        user.setOtpExpiresAt(null);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO updateUserPassword(UserUpdatePasswordDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isOtpVerified()) {
            throw new IllegalArgumentException("OTP is not verified");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtpVerified(false);

        keycloakService.updateUserPassword(user.getEmail(), request.getPassword());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO updateUserStatus(String id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(active);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot delete admin user");
        }

        userRepository.delete(user);
    }
}
