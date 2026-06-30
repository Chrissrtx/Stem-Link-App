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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
        PageRequest pageable = PageRequest.of(0, 10);
        when(notificationRepository.findByUserEmailOrderByCreatedAtDesc(ownerEmail, pageable))
                .thenReturn(new PageImpl<>(List.of(testNotification), pageable, 1));
        when(modelMapper.map(any(), eq(NotificationResponse.class))).thenReturn(new NotificationResponse());

        Page<NotificationResponse> result = notificationService.getMyNotifications(ownerEmail, pageable);

        assertThat(result).hasSize(1);
        verify(notificationRepository).findByUserEmailOrderByCreatedAtDesc(ownerEmail, pageable);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotifications() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(notificationRepository.findByUserEmailOrderByCreatedAtDesc(anyString(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<NotificationResponse> result = notificationService.getMyNotifications(ownerEmail, pageable);

        assertThat(result).isEmpty();
    }
}
