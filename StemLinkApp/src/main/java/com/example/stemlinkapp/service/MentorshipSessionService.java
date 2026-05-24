package com.example.stemlinkapp.service;

import com.example.stemlinkapp.dto.MentorshipSessionResponse;
import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MentorshipSessionService {
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MentorshipSessionService(MentorshipSessionRepository mentorshipSessionRepository, 
                                     ApplicationEventPublisher eventPublisher) {
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<MentorshipSessionResponse> getSessionHistory(String email, String status) {
        return null;
    }

    @Transactional
    public void submitFeedback(Long sessionId, SessionFeedbackDTO feedbackDTO) {
    }

    public void createSessionExample() {
    }
}
