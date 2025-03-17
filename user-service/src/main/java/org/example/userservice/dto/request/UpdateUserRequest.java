package org.example.userservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.userservice.entity.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {
    private String password;
}
