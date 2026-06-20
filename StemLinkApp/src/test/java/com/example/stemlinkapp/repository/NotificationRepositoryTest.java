package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("User Noti");
        testUser.setEmail("noti@test.com");
        testUser.setPassword("Pass123!");
        testUser.setRoles(List.of("STUDENT"));
        entityManager.persist(testUser);
    }

    @Test
    void whenFindByUserEmail_thenReturnNotificationsOrdered() {
        // Given
        Notification n1 = new Notification();
        n1.setUser(testUser);
        n1.setTitle("Oldest");
        n1.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        Notification n2 = new Notification();
        n2.setUser(testUser);
        n2.setTitle("Newest");
        n2.setCreatedAt(LocalDateTime.now());

        entityManager.persist(n1);
        entityManager.persist(n2);
        entityManager.flush();

        // When
        List<Notification> found = notificationRepository.findByUserEmailOrderByCreatedAtDesc("noti@test.com");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getTitle()).isEqualTo("Newest");
        assertThat(found.get(1).getTitle()).isEqualTo("Oldest");
    }
}
