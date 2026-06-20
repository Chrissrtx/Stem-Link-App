package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.MentorshipSession;
import com.example.stemlinkapp.domain.SessionFeedback;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.domain.MentorProfile;
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
public class MentorshipSessionServiceTest {

    @Mock
    private MentorshipSessionRepository mentorshipSessionRepository;

    @Mock
    private SessionFeedbackRepository sessionFeedbackRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MentorshipSessionService mentorshipSessionService;

    private MentorshipSession testSession;
    private SessionFeedbackDTO feedbackDTO;
    private String studentEmail = "student@test.com";

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
    void whenSubmitFeedback_thenSuccess() {
        // Given
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // When
        mentorshipSessionService.submitFeedback(1L, feedbackDTO, studentEmail);

        // Then
        verify(sessionFeedbackRepository, times(1)).save(any(SessionFeedback.class));
        verify(mentorshipSessionRepository, times(1)).save(any(MentorshipSession.class));
    }

    @Test
    void whenSubmitFeedbackByWrongUser_thenThrowException() {
        // Given
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // When & Then
        assertThatThrownBy(() -> mentorshipSessionService.submitFeedback(1L, feedbackDTO, "wrong@test.com"))
                .isInstanceOf(InvalidSessionParticipantException.class);
        verify(sessionFeedbackRepository, never()).save(any());
    }

    @Test
    void whenSubmitDuplicateFeedback_thenThrowException() {
        // Given
        testSession.setFeedback(new SessionFeedback());
        when(mentorshipSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // When & Then
        assertThatThrownBy(() -> mentorshipSessionService.submitFeedback(1L, feedbackDTO, studentEmail))
                .isInstanceOf(FeedbackAlreadySubmittedException.class);
        verify(sessionFeedbackRepository, never()).save(any());
    }
}
