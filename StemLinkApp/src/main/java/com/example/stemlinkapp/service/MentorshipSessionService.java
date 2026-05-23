package com.example.stemlinkapp.service;

import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class MentorshipSessionService {
    private final MentorshipSessionRepository mentorshipSessionRepository;

    public MentorshipSessionService(MentorshipSessionRepository mentorshipSessionRepository) {
        this.mentorshipSessionRepository = mentorshipSessionRepository;
    }
}
