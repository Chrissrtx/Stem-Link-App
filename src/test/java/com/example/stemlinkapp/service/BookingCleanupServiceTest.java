package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.BookingStatus;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingCleanupServiceTest {
    @Mock private BookingRepository bookingRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private BookingCleanupService bookingCleanupService;

    @Test
    void shouldCancelAndNotifyWhenExpiredBookingsExist() {
        User student = new User();
        student.setEmail("student@test.com");
        student.setName("Student");

        Booking expiredBooking = new Booking();
        expiredBooking.setId(1L);
        expiredBooking.setStatus(BookingStatus.PENDING);
        expiredBooking.setStudent(student);
        expiredBooking.setDate(LocalDate.now());
        expiredBooking.setStartTime(LocalTime.now());

        when(bookingRepository.findAllByStatusAndCreatedAtBefore(eq(BookingStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredBooking));

        bookingCleanupService.cancelExpiredPendingBookings();

        assertThat(expiredBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(expiredBooking);
        verify(notificationRepository).save(any());
        verify(emailService).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldDoNothingWhenNoExpiredBookingsExist() {
        when(bookingRepository.findAllByStatusAndCreatedAtBefore(any(), any()))
                .thenReturn(List.of());

        bookingCleanupService.cancelExpiredPendingBookings();

        verify(bookingRepository, never()).save(any());
        verify(notificationRepository, never()).save(any());
    }
}