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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingCleanupServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private BookingCleanupService bookingCleanupService;

    @Test
    void whenCancelExpiredBookings_thenStatusChangedAndNotified() {
        // Given
        User student = new User();
        student.setEmail("student@test.com");
        
        Booking expiredBooking = new Booking();
        expiredBooking.setId(1L);
        expiredBooking.setStatus(BookingStatus.PENDING);
        expiredBooking.setStudent(student);
        expiredBooking.setDate(java.time.LocalDate.now());
        expiredBooking.setStartTime(java.time.LocalTime.now());

        when(bookingRepository.findAllByStatusAndCreatedAtBefore(eq(BookingStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredBooking));

        // When
        bookingCleanupService.cancelExpiredPendingBookings();

        // Then
        assert(expiredBooking.getStatus() == BookingStatus.CANCELLED);
        verify(bookingRepository, times(1)).save(expiredBooking);
        verify(notificationRepository, times(1)).save(any());
        verify(emailService, times(1)).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }
}
