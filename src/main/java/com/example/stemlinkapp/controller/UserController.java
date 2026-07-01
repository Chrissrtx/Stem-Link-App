package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.UserResponse;
import com.example.stemlinkapp.dto.UserPhotoRequest;
import com.example.stemlinkapp.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/users", "/api/users"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PatchMapping("/me/photo")
    public UserResponse updateCurrentUserPhoto(@RequestBody UserPhotoRequest request) {
        return userService.updateCurrentUserPhoto(request.getPhotoUrl());
    }
}
