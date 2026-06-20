package com.example.stemlinkapp.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Ya existe un usuario registrado con el email: " + email);
    }
}
