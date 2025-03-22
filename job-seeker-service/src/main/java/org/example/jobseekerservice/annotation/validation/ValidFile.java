package org.example.jobseekerservice.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "File is required and must be in PDF format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
