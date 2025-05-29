package org.example.common.util;

import org.example.common.enums.UserRole;

public class GetKeycloakRole {
    public static String getKeycloakRole(UserRole role) {
        return switch (role) {
            case UserRole.ADMIN -> "admin";
            case UserRole.RECRUITER -> "recruiter";
            default -> "job_seeker";
        };
    }
}
