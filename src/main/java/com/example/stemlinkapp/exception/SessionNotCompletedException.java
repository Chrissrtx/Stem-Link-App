package com.example.stemlinkapp.exception;

public class SessionNotCompletedException extends RuntimeException {
    public SessionNotCompletedException(String message) {
        super(message);
    }
}
