package org.example.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.User.UserCreationDTO;
import org.example.common.dto.User.UserDTO;
import org.example.common.dto.User.UserUpdateOtpDTO;
import org.example.common.dto.response.ApiResponse;
import org.example.userservice.dto.UserChangePasswordDTO;
import org.example.userservice.dto.UserUpdatePasswordDTO;
import org.example.userservice.dto.UserVerifyOtpDTO;
import org.example.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách user thành công", HttpStatus.OK));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> findPagedUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "role", defaultValue = "ADMIN") String role,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {
        Page<UserDTO> pages = userService.findPagedUsers(query, active, role, page, size, sortBy, direction);
        return ResponseEntity.ok(ApiResponse.successWithPage(pages, "Lấy danh sách user thành công"));
    }

    @GetMapping("ids")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByIds(@RequestParam("ids") List<String> ids) {
        List<UserDTO> users = userService.getUsersByIds(ids != null ? ids : List.of());
        return ResponseEntity.ok(ApiResponse.success(users, "Lấy danh sách user thành công", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin user thành công", HttpStatus.OK));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy thông tin user thành công", HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserCreationDTO request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdUser, "Tạo user thành công", HttpStatus.CREATED));
    }

    @PostMapping("/email-or-create")
    public ResponseEntity<ApiResponse<UserDTO>> getOrCreateUserByEmail(@Valid @RequestBody UserCreationDTO request) {
        UserDTO user = userService.getOrCreateUserByEmail(request);
        return ResponseEntity.ok(ApiResponse.success(user, "Lấy hoặc tạo user thành công", HttpStatus.OK));
    }

    @PutMapping("/update-otp")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserOTP(@Valid @RequestBody UserUpdateOtpDTO request) {
        UserDTO updatedUser = userService.updateUserOtp(request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Update user thành công", HttpStatus.OK));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<UserDTO>> verifyUserOtp(@Valid @RequestBody UserVerifyOtpDTO request) {
        UserDTO verifiedUser = userService.verifyUserOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verifiedUser, "Verify OTP thành công", HttpStatus.OK));
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserPassword(@Valid @RequestBody UserUpdatePasswordDTO request) {
        UserDTO updatedUser = userService.updateUserPassword(request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Update user thành công", HttpStatus.OK));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<UserDTO>> changeUserPassword(@Valid @RequestBody UserChangePasswordDTO request) {
        UserDTO updatedUser = userService.changeUserPassword(request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "Thay đổi mật khẩu thành công", HttpStatus.OK));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(@PathVariable String id) {
        UserDTO blockedUser = userService.updateUserStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success(blockedUser, "Chặn user thành công", HttpStatus.OK));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(@PathVariable String id) {
        UserDTO activatedUser = userService.updateUserStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success(activatedUser, "Kích hoạt user thành công", HttpStatus.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xoá user thành công", HttpStatus.OK));
    }
}
