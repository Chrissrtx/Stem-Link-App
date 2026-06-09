package com.example.stemlinkapp.exception;

public class FeedbackAlreadySubmittedException extends RuntimeException {
    public FeedbackAlreadySubmittedException(String message) {
        super(message);
    }
}
