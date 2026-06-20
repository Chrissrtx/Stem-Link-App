package com.example.stemlinkapp.exception;

public class DuplicateAvailabilityException extends RuntimeException {
    public DuplicateAvailabilityException(String message) {
        super(message);
    }
}