package com.example.stemlinkapp.exception;

public class BookingActionNotAllowedException extends RuntimeException {
    public BookingActionNotAllowedException(String message) {
        super(message);
    }
}