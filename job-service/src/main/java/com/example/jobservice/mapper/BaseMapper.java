package com.example.jobservice.mapper;

import com.example.jobservice.entity.Field;
import com.example.jobservice.entity.Job;
import org.example.common.exception.InvalidDateFormatException;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public interface BaseMapper {
    @Named("jobToJobId")
    default String jobToJobId(Job job) {
        if (job == null) {
            return null;
        }
        return job.getId();
    }

    @Named("fieldToFieldId")
    default String fieldToFieldId(Field field) {
        if (field == null) {
            return null;
        }
        return field.getId();
    }

    @Named("fieldIdToField")
    default Field fieldIdToField(String fieldId) {
        if (fieldId == null) {
            return null;
        }
        Field field = new Field();
        field.setId(fieldId);
        return field;
    }
}
