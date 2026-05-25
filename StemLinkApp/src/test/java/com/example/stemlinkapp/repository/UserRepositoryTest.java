package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Password123!");
        testUser.setRoles(List.of("STUDENT"));
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
        assertThat(found.get().getName()).isEqualTo(testUser.getName());
    }

    @Test
    void whenFindByEmailNotFound_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void whenSaveUser_thenUserIsPersisted() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        User found = entityManager.find(User.class, savedUser.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo(testUser.getEmail());
    }
}
