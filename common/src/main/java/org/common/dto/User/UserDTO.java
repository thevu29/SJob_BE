package org.common.dto.User;

import lombok.Data;
import org.common.enums.UserRole;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String id;
    private String email;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
