package com.example.stemlinkapp.service;

import com.example.stemlinkapp.repository.MentorProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class MentorProfileService {
    private final MentorProfileRepository mentorProfileRepository;

    public MentorProfileService(MentorProfileRepository mentorProfileRepository) {
        this.mentorProfileRepository = mentorProfileRepository;
    }
}
