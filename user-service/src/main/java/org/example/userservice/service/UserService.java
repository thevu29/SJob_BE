package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.NotificationPreference.NotificationPreferenceCreateDTO;
import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.User.UserUpdateOtpDTO;
import org.example.common.enums.UserRole;
import org.example.common.exception.ResourceNotFoundException;
import org.example.common.util.GetKeycloakRole;
import org.example.userservice.client.NotificationPreferenceServiceClient;
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
    private final NotificationPreferenceServiceClient notificationPreferenceServiceClient;

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
        Page<User> userPage;

        if (filterByStatus) {
            userPage = userRepository.findByEmailAndActive(sanitizedQuery, isActive, userRole, pageable);
        } else {
            userPage = userRepository.findByEmail(sanitizedQuery, userRole, pageable);
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        return userMapper.toDto(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        return userMapper.toDto(user);
    }

    public UserDTO getOrCreateUserByEmail(UserCreationDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .map(userMapper::toDto)
                .orElseGet(() -> createUser(request));
    }

    public UserDTO createUser(UserCreationDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        String role = GetKeycloakRole.getKeycloakRole(savedUser.getRole());

        if (savedUser.getGoogleId() == null) {
            keycloakService.createUser(savedUser.getEmail(), request.getPassword(), role);
        }

        NotificationPreferenceCreateDTO notificationPreferenceCreateDTO = NotificationPreferenceCreateDTO.builder()
                .userId(savedUser.getId())
                .build();

        notificationPreferenceServiceClient.createNotificationPreference(notificationPreferenceCreateDTO);

        return userMapper.toDto(savedUser);
    }

    public UserDTO updateUserOtp(UserUpdateOtpDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        if (!passwordEncoder.matches(request.getOtp(), user.getOtp()) ||
                user.getOtpExpiresAt() == null ||
                user.getOtpExpiresAt().isBefore(LocalDateTime.now())
        ) {
            throw new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn");
        }

        user.setOtpVerified(true);
        user.setOtp(null);
        user.setOtpExpiresAt(null);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO updateUserPassword(UserUpdatePasswordDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        if (user.getGoogleId() != null) {
            throw new IllegalArgumentException("Tài khoản này được đăng nhập bằng Google, không thể cập nhật mật khẩu");
        }

        if (!user.isOtpVerified()) {
            throw new IllegalArgumentException("Vui lòng xác minh OTP trước khi cập nhật mật khẩu");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOtpVerified(false);

        keycloakService.updateUserPassword(user.getEmail(), request.getPassword());

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    public UserDTO updateUserStatus(String id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        user.setActive(active);
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản của bạn"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new IllegalArgumentException("Không thể xoá tài khoản ADMIN");
        }

        userRepository.delete(user);
    }
}
