package com.example.jobservice.mapper;

import com.example.jobservice.exception.InvalidDateFormatException;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.YearMonth;
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
}
