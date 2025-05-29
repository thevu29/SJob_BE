package org.example.userservice.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;
    private final RestTemplate restTemplate;
    private final KeycloakProperties properties;
    private final KeycloakClientProperties clientProperties;

    private void assignRealmRoleToUser(String userId, String roleName) {
        RealmResource realmResource = keycloak.realm(properties.getRealm());
        UserResource userResource = realmResource.users().get(userId);

        RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }

    public void createUser(String email, String password, String role) {
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

    public String getAccessToken() {
        String url = clientProperties.getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientProperties.getClientId());
        body.add("client_secret", clientProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        }

        throw new RuntimeException("Failed to get access token from Keycloak");
    }

    public void updateUserPassword(String email, String password) {
        List<UserRepresentation> users = keycloak.realm(properties.getRealm())
                .users()
                .search(email, true);

        if (!users.isEmpty()) {
            String userId = users.getFirst().getId();

            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(password);

            keycloak.realm(properties.getRealm())
                    .users()
                    .get(userId)
                    .resetPassword(passwordCred);
        }
    }
}
