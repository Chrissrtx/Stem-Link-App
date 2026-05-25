package com.example.stemlinkapp;

import com.example.stemlinkapp.domain.*;
import com.example.stemlinkapp.dto.BookingRequest;
import com.example.stemlinkapp.dto.BookingStatusRequest;
import com.example.stemlinkapp.exception.InvalidOperationException;
import com.example.stemlinkapp.exception.UnauthorizedException;
import com.example.stemlinkapp.repository.AvailabilityBlockRepository;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.UserRepository;
import com.example.stemlinkapp.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock AvailabilityBlockRepository availabilityBlockRepository;
    @Mock MentorProfileRepository mentorProfileRepository;
    @Mock UserRepository userRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks BookingService bookingService;

    User student;
    User mentorUser;
    MentorProfile mentorProfile;
    AvailabilityBlock block;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setEmail("student@test.com");
        student.setName("Estudiante");

        mentorUser = new User();
        mentorUser.setId(2L);
        mentorUser.setEmail("mentor@test.com");
        mentorUser.setName("Mentor");

        mentorProfile = new MentorProfile();
        mentorProfile.setId(1L);
        mentorProfile.setUser(mentorUser);

        block = new AvailabilityBlock();
        block.setId(1L);
        block.setDayOfWeek(DayOfWeek.MONDAY);
        block.setStartTime(LocalTime.of(10, 0));
        block.setEndTime(LocalTime.of(11, 0));
        block.setMentorProfile(mentorProfile);
    }

    @Test
    void shouldCreateBookingSuccessfully() {
        BookingRequest request = new BookingRequest();
        request.setMentorProfileId(1L);
        request.setAvailabilityBlockId(1L);
        request.setDate(LocalDate.now().plusDays(7)); // lunes futuro

        // Ajustar fecha al día correcto del bloque
        LocalDate nextMonday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
        request.setDate(nextMonday);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(mentorProfileRepository.findById(1L)).thenReturn(Optional.of(mentorProfile));
        when(availabilityBlockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(bookingRepository.existsConflictingBooking(any(), any(), any(), any())).thenReturn(false);

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setStudent(student);
        savedBooking.setMentor(mentorProfile);
        savedBooking.setDate(nextMonday);
        savedBooking.setStartTime(block.getStartTime());
        savedBooking.setEndTime(block.getEndTime());
        savedBooking.setStatus(BookingStatus.PENDING);

        when(bookingRepository.save(any())).thenReturn(savedBooking);

        var response = bookingService.createBooking("student@test.com", request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
        verify(eventPublisher, times(1)).publishEvent(any());
    }

    @Test
    void shouldFailWhenBlockDoesNotBelongToMentor() {
        MentorProfile otherMentor = new MentorProfile();
        otherMentor.setId(99L);
        otherMentor.setUser(mentorUser);
        block.setMentorProfile(otherMentor);

        BookingRequest request = new BookingRequest();
        request.setMentorProfileId(1L);
        request.setAvailabilityBlockId(1L);
        request.setDate(LocalDate.now().plusDays(7));

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(mentorProfileRepository.findById(1L)).thenReturn(Optional.of(mentorProfile));
        when(availabilityBlockRepository.findById(1L)).thenReturn(Optional.of(block));

        assertThrows(InvalidOperationException.class,
                () -> bookingService.createBooking("student@test.com", request));
    }

    @Test
    void shouldFailWhenStudentTriesToConfirmBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStudent(student);
        booking.setMentor(mentorProfile);
        booking.setStatus(BookingStatus.PENDING);

        BookingStatusRequest request = new BookingStatusRequest();
        request.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(UnauthorizedException.class,
                () -> bookingService.updateStatus("student@test.com", 1L, request));
    }
}