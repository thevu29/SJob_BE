package com.example.jobservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.response.ApiResponse;
import org.example.common.exception.FileUploadException;
import org.example.common.exception.InvalidDateFormatException;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Access denied: You don't have permission to access this resource",
                HttpStatus.FORBIDDEN);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
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

        if (errorMessage.contains("LocationType")) {
            ApiResponse<Object> response = ApiResponse.error(
                    "Invalid location type value. Allowed values: ONSITE, REMOTE, HYBRID",
                    HttpStatus.BAD_REQUEST);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        } else if (errorMessage.contains("EmployeeType")) {
            ApiResponse<Object> response = ApiResponse.error(
                    "Invalid employee type value. Allowed values: FULL_TIME, PART_TIME, INTERNSHIP, FREE_LANCE",
                    HttpStatus.BAD_REQUEST);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        } else if (errorMessage.contains("LocalDate")) {
            ApiResponse<Object> response = ApiResponse.error(
                    "Invalid date format. Please use ISO format (yyyy-MM-dd)",
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

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        ApiResponse<Object> response = ApiResponse.error(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<Object>> handleDateTimeParseException(DateTimeParseException ex) {
        log.error("Invalid date format: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                "Invalid date format. Please use ISO format (yyyy-MM-dd)",
                HttpStatus.BAD_REQUEST);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileUploadException(FileUploadException ex) {
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}

