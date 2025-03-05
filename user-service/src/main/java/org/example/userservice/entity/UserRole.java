package org.example.userservice.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum UserRole {
    JOB_SEEKER("Job Seeker"),
    RECRUITER("Recruiter"),
    ADMIN("Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    @JsonCreator
    public static UserRole fromString(String value) {
        if (value == null) {
            return null;
        }

        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + value);
    }
}
