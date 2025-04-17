package com.example.jobservice.mapper;

import com.example.jobservice.entity.Field;
import org.common.exception.InvalidDateFormatException;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public interface BaseMapper {
    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException("Date must be in format yyyy-MM-dd");
        }
    }

    @Named("fieldToFieldId")
    default String fieldToFieldId(Field field) {
        if (field == null) {
            return null;
        }
        return field.getId();
    }

    @Named("fieldIdToField")
    default Field fieldToFieldId(String fieldId) {
        if (fieldId == null) {
            return null;
        }
        Field field = new Field();
        field.setId(fieldId);
        return field;
    }
}
