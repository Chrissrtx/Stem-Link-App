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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private final String ownerEmail = "owner@test.com";

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setEmail(ownerEmail);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setUser(owner);
        testNotification.setRead(false);
    }

    @Test
    void shouldMarkNotificationAsReadWhenOwnerRequests() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        notificationService.markAsRead(1L, ownerEmail);

        assertThat(testNotification.isRead()).isTrue();
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void shouldThrowExceptionWhenNonOwnerTriesToMarkAsRead() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L, "intruder@test.com"))
                .isInstanceOf(UnauthorizedNotificationAccessException.class);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNotificationDoesNotExist() {
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99L, ownerEmail))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void shouldReturnNotificationsWhenUserHasSome() {
        when(notificationRepository.findByUserEmailOrderByCreatedAtDesc(ownerEmail))
                .thenReturn(List.of(testNotification));
        when(modelMapper.map(any(), eq(NotificationResponse.class))).thenReturn(new NotificationResponse());

        List<NotificationResponse> result = notificationService.getMyNotifications(ownerEmail);

        assertThat(result).hasSize(1);
        verify(notificationRepository).findByUserEmailOrderByCreatedAtDesc(ownerEmail);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotifications() {
        when(notificationRepository.findByUserEmailOrderByCreatedAtDesc(anyString()))
                .thenReturn(List.of());

        List<NotificationResponse> result = notificationService.getMyNotifications(ownerEmail);

        assertThat(result).isEmpty();
    }
}