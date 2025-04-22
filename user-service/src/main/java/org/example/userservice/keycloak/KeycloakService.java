package org.example.userservice.keycloak;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    public void updateUserPassword(String email, String password) {
        List<UserRepresentation> users = keycloak.realm(properties.getRealm())
                .users()
                .search(email, true);

        if (!users.isEmpty()) {
            String userId = users.getFirst().getId();
            System.out.println("userId: " + userId);

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
