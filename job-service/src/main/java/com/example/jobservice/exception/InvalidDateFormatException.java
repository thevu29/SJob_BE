package com.example.jobservice.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}
