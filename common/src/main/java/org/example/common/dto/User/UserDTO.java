package org.example.common.dto.User;

import lombok.Data;
import org.example.common.enums.UserRole;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;
    private String email;
    private UserRole role;
    private boolean active;
    private String googleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
