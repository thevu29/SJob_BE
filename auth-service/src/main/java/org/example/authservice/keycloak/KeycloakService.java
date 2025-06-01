package org.example.authservice.keycloak;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.GoogleLoginDTO;
import org.example.common.dto.Auth.TokenDTO;
import org.example.authservice.dto.LoginDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KeycloakService {
    private final RestTemplate restTemplate;
    private final KeycloakClientProperties clientProperties;
    private final KeycloakGoogleClientProperties googleClientProperties;

    private final String TOKEN_URL = "http://localhost:9090/realms/sjob/protocol/openid-connect/token";

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

        String url = "http://localhost:9090/realms/sjob/protocol/openid-connect/userinfo";

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                url,
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

        String url = "http://localhost:9090/realms/sjob/protocol/openid-connect/logout";

        restTemplate.postForEntity(url, request, Void.class);
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
