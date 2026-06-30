package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.dto.NotificationResponse;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import com.example.stemlinkapp.exception.NotificationNotFoundException;
import com.example.stemlinkapp.exception.UnauthorizedNotificationAccessException;
import com.example.stemlinkapp.repository.NotificationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public NotificationService(NotificationRepository notificationRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(String email, Pageable pageable) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email, pageable)
                .map(n -> modelMapper.map(n, NotificationResponse.class));
    }

    @Transactional
    public void markAsRead(Long id, String email) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));

        if (!notification.getUser().getEmail().equals(email)) {
            throw new UnauthorizedNotificationAccessException("You are not authorized to access this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Async
    @EventListener
    public void handleMentorshipSessionCreated(MentorshipSessionCreatedEvent event) {
        Notification notification = new Notification();
        notification.setTitle("New Mentorship Request");
        notification.setMessage("Student " + event.getBooking().getStudent().getName() + " has requested a session.");
        notification.setUser(event.getBooking().getMentor().getUser());
        notificationRepository.save(notification);
    }
}
