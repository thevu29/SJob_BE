package org.example.common.annotation.File;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(List.of(
            "application/pdf"
    ));

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
    }
}
