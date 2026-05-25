package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.BookingStatus;
import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingCleanupService {

    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public BookingCleanupService(BookingRepository bookingRepository,
                                 NotificationRepository notificationRepository,
                                 EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cancelExpiredPendingBookings() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusHours(48);
        List<Booking> expiredBookings = bookingRepository.findAllByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, expirationThreshold);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            Notification notification = new Notification();
            notification.setUser(booking.getStudent());
            notification.setTitle("Solicitud de reserva expirada");
            notification.setMessage("Tu solicitud de mentoría para el " + booking.getDate() + 
                                   " ha expirado tras 48h sin respuesta.");
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);

            Map<String, Object> vars = new HashMap<>();
            vars.put("date", booking.getDate().toString());
            vars.put("startTime", booking.getStartTime().toString());
            emailService.sendHtmlMessage(booking.getStudent().getEmail(), 
                                       "Solicitud Expirada - STEM Link", 
                                       "booking-cancelled.html", vars);
            
            System.out.println("Reserva ID " + booking.getId() + " cancelada automáticamente por expiración.");
        }
    }
}
