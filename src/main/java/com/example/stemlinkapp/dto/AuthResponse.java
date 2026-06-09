package com.example.stemlinkapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;
}
