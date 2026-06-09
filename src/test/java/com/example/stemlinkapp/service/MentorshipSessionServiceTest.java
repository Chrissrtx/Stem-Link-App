package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.*;
import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.exception.FeedbackAlreadySubmittedException;
import com.example.stemlinkapp.exception.InvalidSessionParticipantException;
import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import com.example.stemlinkapp.repository.SessionFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipSessionServiceTest {

    @Mock private MentorshipSessionRepository mentorshipSessionRepository;
    @Mock private SessionFeedbackRepository sessionFeedbackRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private MentorshipSessionService mentorshipSessionService;

    private MentorshipSession testSession;
    private SessionFeedbackDTO feedbackDTO;
    private final String studentEmail = "student@test.com";

    @BeforeEach
    void setUp() {
        User student = new User();
        student.setEmail(studentEmail);

        User mentorUser = new User();
        mentorUser.setEmail("mentor@test.com");

        MentorProfile mentor = new MentorProfile();
        mentor.setUser(mentorUser);

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setMentor(mentor);

        testSession = new MentorshipSession();
        testSession.setId(1L);
        testSession.setBooking(booking);

        feedbackDTO = new SessionFeedbackDTO();
        feedbackDTO.setStars(5);
        feedbackDTO.setMentorComments("Great session!");
    }

    @Test
    void shouldSaveFeedbackWhenStudentSubmitsFirst() {
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        mentorshipSessionService.submitFeedback(1L, feedbackDTO, studentEmail);

        verify(sessionFeedbackRepository).save(any(SessionFeedback.class));
        verify(mentorshipSessionRepository).save(any(MentorshipSession.class));
    }

    @Test
    void shouldThrowExceptionWhenNonParticipantSubmitsFeedback() {
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        assertThatThrownBy(() -> mentorshipSessionService.submitFeedback(1L, feedbackDTO, "outsider@test.com"))
                .isInstanceOf(InvalidSessionParticipantException.class);
        verify(sessionFeedbackRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFeedbackAlreadySubmitted() {
        testSession.setFeedback(new SessionFeedback());
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        assertThatThrownBy(() -> mentorshipSessionService.submitFeedback(1L, feedbackDTO, studentEmail))
                .isInstanceOf(FeedbackAlreadySubmittedException.class);
        verify(sessionFeedbackRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSessionDoesNotExist() {
        when(mentorshipSessionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mentorshipSessionService.submitFeedback(99L, feedbackDTO, studentEmail))
                .isInstanceOf(RuntimeException.class);
    }
}