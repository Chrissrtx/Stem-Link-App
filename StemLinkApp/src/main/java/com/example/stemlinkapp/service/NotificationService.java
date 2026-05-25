package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.NotificationResponse;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import com.example.stemlinkapp.repository.NotificationRepository;
import com.example.stemlinkapp.exception.NotificationNotFoundException;
import com.example.stemlinkapp.exception.UnauthorizedNotificationAccessException;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    public NotificationService(NotificationRepository notificationRepository, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(n -> modelMapper.map(n, NotificationResponse.class))
                .collect(Collectors.toList());
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
