package com.example.stemlinkapp.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("No se encontró un usuario con el email: " + email);
    }
}
