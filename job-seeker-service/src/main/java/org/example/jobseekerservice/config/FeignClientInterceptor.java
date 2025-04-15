package org.example.jobseekerservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.example.jobseekerservice.keycloak.KeycloakService;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignClientInterceptor implements RequestInterceptor {
    private final KeycloakService keycloakService;

    @Override
    public void apply(RequestTemplate template) {
        String token = keycloakService.getAccessToken();
        template.header("Authorization", "Bearer " + token);
    }
}
