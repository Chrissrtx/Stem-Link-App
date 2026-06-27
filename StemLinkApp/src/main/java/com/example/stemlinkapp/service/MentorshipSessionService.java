package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.BookingStatus;
import com.example.stemlinkapp.domain.MentorshipSession;
import com.example.stemlinkapp.domain.SessionFeedback;
import com.example.stemlinkapp.dto.MentorshipSessionResponse;
import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.exception.FeedbackAlreadySubmittedException;
import com.example.stemlinkapp.exception.InvalidSessionParticipantException;
import com.example.stemlinkapp.exception.SessionNotFoundException;
import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import com.example.stemlinkapp.repository.SessionFeedbackRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorshipSessionService {
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final SessionFeedbackRepository sessionFeedbackRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public MentorshipSessionService(MentorshipSessionRepository mentorshipSessionRepository,
                                     SessionFeedbackRepository sessionFeedbackRepository,
                                     ModelMapper modelMapper,
                                     ApplicationEventPublisher eventPublisher) {
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.sessionFeedbackRepository = sessionFeedbackRepository;
        this.modelMapper = modelMapper;
        this.eventPublisher = eventPublisher;
    }

    private MentorshipSessionResponse toResponse(MentorshipSession session) {
        MentorshipSessionResponse response = new MentorshipSessionResponse();
        response.setId(session.getId());
        response.setTopic(session.getTopic());
        Booking booking = session.getBooking();
        if (booking != null) {
            response.setStatus(booking.getStatus() != null ? booking.getStatus().name() : null);
            response.setDate(booking.getDate());
            response.setStartTime(booking.getStartTime());
            response.setEndTime(booking.getEndTime());
            if (booking.getMentor() != null && booking.getMentor().getUser() != null) {
                response.setMentorName(booking.getMentor().getUser().getName());
            }
            if (booking.getStudent() != null) {
                response.setStudentName(booking.getStudent().getName());
            }
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<MentorshipSessionResponse> getSessionHistory(String email, String status) {
        BookingStatus bookingStatus = null;
        if (status != null && !status.isBlank()) {
            try {
                bookingStatus = BookingStatus.valueOf(status.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid status and don't filter by it
            }
        }
        return mentorshipSessionRepository.findByUserEmailAndStatus(email, bookingStatus).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

        SessionFeedback feedback = new SessionFeedback();
        feedback.setStars(feedbackDTO.getStars());
        feedback.setMentorComments(feedbackDTO.getMentorComments());
        feedback.setImpactRecord(feedbackDTO.getImpactRecord());
        feedback.setMentorshipSession(session);

        sessionFeedbackRepository.save(feedback);
        session.setFeedback(feedback);
        mentorshipSessionRepository.save(session);
    }

    public void createSessionExample() {
    }
}
