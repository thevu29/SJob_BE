package org.example.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class CorsFilterConfig {
    @Bean
    public GatewayFilter corsFilter() {
        return (exchange, chain) -> {
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .response(exchange.getResponse())
                    .build();

            HttpHeaders headers = modifiedExchange.getResponse().getHeaders();
            headers.add("Access-Control-Allow-Origin", "http://localhost:3000");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "*");
            headers.add("Access-Control-Allow-Credentials", "true");

            return chain.filter(modifiedExchange);
        };
    }
}