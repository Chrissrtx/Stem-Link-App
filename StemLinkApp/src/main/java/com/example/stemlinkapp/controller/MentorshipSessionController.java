package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.service.MentorshipSessionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentorship-sessions")
public class MentorshipSessionController {
    private final MentorshipSessionService mentorshipSessionService;

    public MentorshipSessionController(MentorshipSessionService mentorshipSessionService) {
        this.mentorshipSessionService = mentorshipSessionService;
    }
}
