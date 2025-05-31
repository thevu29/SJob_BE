package org.example.authservice.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.GoogleLoginDTO;
import org.example.common.dto.Auth.TokenDTO;
import org.example.common.dto.JobSeeker.JobSeekerCreationDTO;
import org.example.common.dto.Recruiter.RecruiterCreationDTO;
import org.example.authservice.dto.LoginDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    private final KeycloakProperties properties;
    private final KeycloakClientProperties clientProperties;
    private final KeycloakGoogleClientProperties googleClientProperties;

    private String TOKEN_URL;
    private String LOGOUT_URL;
    private String USER_INFO_URL;

    @PostConstruct
    private void init() {
        TOKEN_URL = "%s/realms/%s/protocol/openid-connect/token"
                .formatted(properties.getServerUrl(), properties.getRealm());

        LOGOUT_URL = "%s/realms/%s/protocol/openid-connect/logout"
                .formatted(properties.getServerUrl(), properties.getRealm());

        USER_INFO_URL = "%s/realms/%s/protocol/openid-connect/userinfo"
                .formatted(properties.getServerUrl(), properties.getRealm());
    }

    private void assignRealmRoleToUser(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(properties.getRealm());
        UserResource userResource = realmResource.users().get(userId);

        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }

    private void createUserInKeycloak(String email, String password, String role) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email);
        user.setEmail(email);
        user.setEmailVerified(true);

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);

        user.setCredentials(List.of(cred));

        try (Response response = keycloak.realm(properties.getRealm()).users().create(user)) {
            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
            } else {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

                CredentialRepresentation passwordCred = new CredentialRepresentation();
                passwordCred.setTemporary(false);
                passwordCred.setType(CredentialRepresentation.PASSWORD);
                passwordCred.setValue(password);

                keycloak.realm(properties.getRealm())
                        .users()
                        .get(userId)
                        .resetPassword(passwordCred);

                assignRealmRoleToUser(userId, role);
            }
        }
    }

    public void createJobSeeker(JobSeekerCreationDTO request) {
        createUserInKeycloak(request.getEmail(), request.getPassword(), "job_seeker");
    }

    public void createRecruiter(RecruiterCreationDTO request) {
        createUserInKeycloak(request.getEmail(), request.getPassword(), "recruiter");
    }

    public String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientProperties.getClientId());
        body.add("client_secret", clientProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        return Objects.requireNonNull(response.getBody()).get("access_token").toString();
    }

    public TokenDTO refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        body.add("client_id", clientProperties.getClientId());
        body.add("client_secret", clientProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TokenDTO> response = restTemplate.postForEntity(TOKEN_URL, request, TokenDTO.class);

        return response.getBody();
    }

    public GoogleLoginDTO googleLogin(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("client_id", googleClientProperties.getClientId());
        params.add("client_secret", googleClientProperties.getClientSecret());
        params.add("redirect_uri", googleClientProperties.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<TokenDTO> response = restTemplate.postForEntity(TOKEN_URL, request, TokenDTO.class);

        TokenDTO token = response.getBody();

        if (token == null || token.getAccessToken() == null) {
            throw new RuntimeException("Failed to obtain access token from Google");
        }

        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(token.getAccessToken());
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                userInfoRequest,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();

        if (userInfo == null) {
            throw new RuntimeException("Failed to obtain user info from Google");
        }

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String googleId = (String) userInfo.get("sub");
        String image = (String) userInfo.get("picture");

        return GoogleLoginDTO.builder()
                .email(email)
                .name(name)
                .googleId(googleId)
                .image(image)
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresIn(token.getExpiresIn())
                .build();
    }

    public TokenDTO login(LoginDTO data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientProperties.getClientId());
        body.add("client_secret", clientProperties.getClientSecret());
        body.add("username", data.getEmail());
        body.add("password", data.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TokenDTO> response = restTemplate.postForEntity(TOKEN_URL, request, TokenDTO.class);

        return response.getBody();
    }

    public void logout(String refreshToken) {
        String clientId = extractClientIdFromRefreshToken(refreshToken);

        if (clientId == null) {
            throw new RuntimeException("Cannot extract client_id from refresh token");
        }

        String clientSecret;

        if (clientId.equals(clientProperties.getClientId())) {
            clientSecret = clientProperties.getClientSecret();
        } else if (clientId.equals(googleClientProperties.getClientId())) {
            clientSecret = googleClientProperties.getClientSecret();
        } else {
            throw new RuntimeException("Unknown client_id: " + clientId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(LOGOUT_URL, request, Void.class);
    }

    private String extractClientIdFromRefreshToken(String refreshToken) {
        try {
            String[] parts = refreshToken.split("\\.");
            if (parts.length < 2) return null;

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode json = objectMapper.readTree(payload);

            return json.has("azp") ? json.get("azp").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
