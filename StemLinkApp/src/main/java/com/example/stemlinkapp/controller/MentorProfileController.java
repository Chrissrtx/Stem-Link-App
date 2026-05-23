package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.service.MentorProfileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentors")
public class MentorProfileController {
    private final MentorProfileService mentorProfileService;

    public MentorProfileController(MentorProfileService mentorProfileService) {
        this.mentorProfileService = mentorProfileService;
    }
}
