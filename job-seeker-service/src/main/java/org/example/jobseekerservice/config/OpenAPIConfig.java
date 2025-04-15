package org.example.jobseekerservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Value("${gateway.url}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Seeker Service API")
                        .version("1.0")
                        .description("API documentation for the Job Seeker Service")
                )
                .servers(List.of(
                        new Server().url(gatewayUrl).description("Local server")
                ));
    }
}
