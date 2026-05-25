package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.NotificationResponse;
import com.example.stemlinkapp.exception.NotificationNotFoundException;
import com.example.stemlinkapp.exception.UnauthorizedNotificationAccessException;
import com.example.stemlinkapp.repository.NotificationRepository;
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
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private User testUser;
    private String userEmail = "owner@test.com";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail(userEmail);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(testUser);
        testNotification.setRead(false);
    }

    @Test
    void whenMarkAsRead_thenSuccess() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When
        notificationService.markAsRead(1L, userEmail);

        // Then
        verify(notificationRepository, times(1)).save(testNotification);
        assert(testNotification.isRead());
    }

    @Test
    void whenMarkAsReadByWrongUser_thenThrowException() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsRead(1L, "intruder@test.com"))
                .isInstanceOf(UnauthorizedNotificationAccessException.class);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void whenMarkNonExistentAsRead_thenThrowException() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> notificationService.markAsRead(1L, userEmail))
                .isInstanceOf(NotificationNotFoundException.class);
    }
}
