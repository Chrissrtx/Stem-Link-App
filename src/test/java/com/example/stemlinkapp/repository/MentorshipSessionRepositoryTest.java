package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.AbstractIntegrationTest;
import com.example.stemlinkapp.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MentorshipSessionRepositoryTest extends AbstractIntegrationTest {

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
        student.setEmail("student_session@test.com");
        student.setPassword("Pass123!");
        student.setRoles(List.of("STUDENT"));
        entityManager.persist(student);

        User mentorUser = new User();
        mentorUser.setName("Mentor");
        mentorUser.setEmail("mentor_session@test.com");
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
        testBooking.setCreatedAt(LocalDateTime.now());
        entityManager.persist(testBooking);

        testSession = new MentorshipSession();
        testSession.setBooking(testBooking);
        testSession.setNotes("Integration test notes");
        entityManager.persist(testSession);

        entityManager.flush();
    }

    @Test
    void shouldPersistSessionWhenSaved() {
        assertThat(testSession.getId()).isNotNull();
        MentorshipSession found = entityManager.find(MentorshipSession.class, testSession.getId());
        assertThat(found.getNotes()).isEqualTo("Integration test notes");
    }

    @Test
    void shouldReturnSessionWhenFindById() {
        Optional<MentorshipSession> found = mentorshipSessionRepository.findById(testSession.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNotes()).isEqualTo("Integration test notes");
        assertThat(found.get().getBooking().getId()).isEqualTo(testBooking.getId());
    }

    @Test
    void shouldReturnEmptyWhenSessionIdDoesNotExist() {
        Optional<MentorshipSession> found = mentorshipSessionRepository.findById(999L);

        assertThat(found).isEmpty();
    }
}