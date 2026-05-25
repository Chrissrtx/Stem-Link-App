package com.example.stemlinkapp.exception;

public class UnauthorizedNotificationAccessException extends RuntimeException {
    public UnauthorizedNotificationAccessException(String message) {
        super(message);
    }
}
