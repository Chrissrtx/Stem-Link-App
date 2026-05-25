package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorshipSession;
import com.example.stemlinkapp.dto.MentorshipSessionResponse;
import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import com.example.stemlinkapp.exception.FeedbackAlreadySubmittedException;
import com.example.stemlinkapp.exception.InvalidSessionParticipantException;
import com.example.stemlinkapp.exception.SessionNotFoundException;
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
    public void submitFeedback(Long sessionId, SessionFeedbackDTO feedbackDTO, String email) {
        MentorshipSession session = mentorshipSessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found with ID: " + sessionId));

        if (!session.getBooking().getStudent().getEmail().equals(email)) {
            throw new InvalidSessionParticipantException("You are not authorized to leave feedback for this session");
        }

        if (session.getFeedback() != null) {
            throw new FeedbackAlreadySubmittedException("Feedback has already been submitted for this session");
        }

    }

    public void createSessionExample() {
    }
}
