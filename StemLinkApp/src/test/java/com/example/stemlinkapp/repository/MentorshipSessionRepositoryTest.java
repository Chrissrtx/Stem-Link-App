package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.MentorshipSession;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MentorshipSessionRepositoryTest {

    @Autowired
    private MentorshipSessionRepository mentorshipSessionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MentorshipSession testSession;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        User student = new User();
        student.setName("Student");
        student.setEmail("student_repo@test.com");
        student.setPassword("Pass123!");
        student.setRoles(List.of("STUDENT"));
        entityManager.persist(student);

        User mentorUser = new User();
        mentorUser.setName("Mentor");
        mentorUser.setEmail("mentor_repo@test.com");
        mentorUser.setPassword("Pass123!");
        mentorUser.setRoles(List.of("MENTOR"));
        entityManager.persist(mentorUser);

        MentorProfile mentorProfile = new MentorProfile();
        mentorProfile.setUser(mentorUser);
        entityManager.persist(mentorProfile);

        testBooking = new Booking();
        testBooking.setStudent(student);
        testBooking.setMentor(mentorProfile);
        testBooking.setDate(LocalDate.now());
        testBooking.setStartTime(LocalTime.of(10, 0));
        testBooking.setEndTime(LocalTime.of(11, 0));
        testBooking.setStatus(BookingStatus.CONFIRMED);
        entityManager.persist(testBooking);

        testSession = new MentorshipSession();
        testSession.setBooking(testBooking);
        testSession.setNotes("Test Notes");
    }

    @Test
    void whenSaveSession_thenPersisted() {
        // When
        MentorshipSession saved = mentorshipSessionRepository.save(testSession);

        // Then
        assertThat(saved.getId()).isNotNull();
        MentorshipSession found = entityManager.find(MentorshipSession.class, saved.getId());
        assertThat(found.getNotes()).isEqualTo("Test Notes");
        assertThat(found.getBooking().getId()).isEqualTo(testBooking.getId());
    }

    @Test
    void whenFindById_thenReturnSession() {
        // Given
        entityManager.persist(testSession);
        entityManager.flush();

        // When
        Optional<MentorshipSession> found = mentorshipSessionRepository.findById(testSession.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getNotes()).isEqualTo("Test Notes");
    }
}
