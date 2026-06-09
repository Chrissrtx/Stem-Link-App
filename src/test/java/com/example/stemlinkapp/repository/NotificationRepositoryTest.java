package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.AbstractIntegrationTest;
import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Noti User");
        testUser.setEmail("noti@test.com");
        testUser.setPassword("Pass123!");
        testUser.setRoles(List.of("STUDENT"));
        entityManager.persist(testUser);
        entityManager.flush();
    }

    @Test
    void shouldReturnNotificationsOrderedByDateDescWhenUserHasNotifications() {
        Notification oldest = new Notification();
        oldest.setUser(testUser);
        oldest.setTitle("Oldest");
        oldest.setCreatedAt(LocalDateTime.now().minusDays(2));
        entityManager.persist(oldest);

        Notification newest = new Notification();
        newest.setUser(testUser);
        newest.setTitle("Newest");
        newest.setCreatedAt(LocalDateTime.now());
        entityManager.persist(newest);

        entityManager.flush();

        List<Notification> found = notificationRepository.findByUserEmailOrderByCreatedAtDesc("noti@test.com");

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getTitle()).isEqualTo("Newest");
        assertThat(found.get(1).getTitle()).isEqualTo("Oldest");
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotifications() {
        List<Notification> found = notificationRepository.findByUserEmailOrderByCreatedAtDesc("noti@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenEmailDoesNotExist() {
        List<Notification> found = notificationRepository.findByUserEmailOrderByCreatedAtDesc("nonexistent@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldPersistNotificationWhenSaved() {
        Notification notification = new Notification();
        notification.setUser(testUser);
        notification.setTitle("Test");
        notification.setMessage("Test message");
        notification.setCreatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        assertThat(saved.getId()).isNotNull();
        Notification found = entityManager.find(Notification.class, saved.getId());
        assertThat(found.getTitle()).isEqualTo("Test");
        assertThat(found.isRead()).isFalse();
    }
}