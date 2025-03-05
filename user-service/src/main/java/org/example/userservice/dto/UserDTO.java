package org.example.userservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.userservice.entity.UserRole;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String id;
    private String email;
    private UserRole role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
