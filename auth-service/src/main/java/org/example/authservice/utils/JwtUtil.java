package org.example.authservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.example.common.enums.UserRole;

import java.util.List;
import java.util.Map;

public class JwtUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DecodedJWT decodeJwt(String token) {
        return JWT.decode(token);
    }

    public static String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    public static List<String> getRolesFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);

        Map<String, Object> realmAccess = decodedJWT.getClaim("realm_access").asMap();

        Object rolesObj = realmAccess.get("roles");

        return objectMapper.convertValue(
                rolesObj,
                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
        );
    }

    public static UserRole getRoleFromToken(String token) {
        List<String> roles = getRolesFromToken(token);

        if (roles.contains("admin")) {
            return UserRole.ADMIN;
        } else if (roles.contains("recruiter")) {
            return UserRole.RECRUITER;
        } else {
            return UserRole.JOB_SEEKER;
        }
    }
}
