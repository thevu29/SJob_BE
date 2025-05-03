package org.example.userservice.keycloak;

import lombok.RequiredArgsConstructor;
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
