package com.example.jobservice.utils.validations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.example.common.exception.FileUploadException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ValidationUtils {
    private final Validator validator;

    public ValidationUtils(ValidatorFactory factory) {
        this.validator = factory.getValidator();
    }

    public <T> void validateCSVRecord(T record, int rowNumber) {
        Set<ConstraintViolation<T>> violations = validator.validate(record);
        if (!violations.isEmpty()) {
            String errorMessages = violations.stream()
                    .map(violation -> String.format("Dòng %d: %s", rowNumber, violation.getMessage()))
                    .collect(Collectors.joining(", "));
            throw new FileUploadException(errorMessages);
        }
    }
}
