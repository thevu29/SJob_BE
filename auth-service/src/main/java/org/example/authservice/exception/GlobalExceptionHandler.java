package org.example.authservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.SerializationException;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(Exception e) {
        log.error("Internal server error: ", e);

        ApiResponse<Object> response = ApiResponse.error(
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(Exception ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Object> response = ApiResponse.validationError(validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = ex.getMessage();
        log.warn("Message not readable: {}", errorMessage);

        if (errorMessage.contains("UserRole")) {
            ApiResponse<Object> response = ApiResponse.error(
                    "Invalid role value. Allowed values: JOB_SEEKER, RECRUITER, ADMIN",
                    HttpStatus.BAD_REQUEST);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        ApiResponse<Object> response = ApiResponse.error(
                "Invalid request format",
                HttpStatus.BAD_REQUEST);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleKeycloakWebAppException(WebApplicationException ex) {
        log.error("Keycloak WebApplicationException: ", ex);
        ApiResponse<Object> response = ApiResponse.error("Keycloak error: " + ex.getMessage(), HttpStatus.BAD_GATEWAY);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(ProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleKeycloakProcessingException(ProcessingException ex) {
        log.error("Keycloak ProcessingException: ", ex);
        ApiResponse<Object> response = ApiResponse.error("Keycloak connection error", HttpStatus.GATEWAY_TIMEOUT);
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("Authentication failed: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("Access denied", HttpStatus.FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("JSON processing error: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
                "Failed to process message: Invalid format",
                HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<ApiResponse<Object>> handleKafkaException(KafkaException ex) {
        log.error("Kafka processing error: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
                "Message processing failed: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(SerializationException.class)
    public ResponseEntity<ApiResponse<Object>> handleKafkaSerializationException(SerializationException ex) {
        log.error("Kafka serialization error: ", ex);
        ApiResponse<Object> response = ApiResponse.error(
                "Failed to process message: Serialization error",
                HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(HttpClientErrorException.Unauthorized ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        ApiResponse<Object> response = ApiResponse.error("Invalid credentials", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
