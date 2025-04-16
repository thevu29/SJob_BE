package com.example.recruiterservice.config;

import com.example.recruiterservice.keycloak.KeycloakService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
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
