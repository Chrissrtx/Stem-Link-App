package com.example.stemlinkapp.service;

import com.example.stemlinkapp.repository.SessionFeedbackRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionFeedbackService {
    private final SessionFeedbackRepository sessionFeedbackRepository;

    public SessionFeedbackService(SessionFeedbackRepository sessionFeedbackRepository) {
        this.sessionFeedbackRepository = sessionFeedbackRepository;
    }
}
