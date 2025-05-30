package org.example.reportservice.keycloak;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak.client")
public class KeycloakClientProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUri;
}
