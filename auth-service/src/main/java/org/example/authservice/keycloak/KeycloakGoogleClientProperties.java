package org.example.authservice.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak.google-client")
public class KeycloakGoogleClientProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
