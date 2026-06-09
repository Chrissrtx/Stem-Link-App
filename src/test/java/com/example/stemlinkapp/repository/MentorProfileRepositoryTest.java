package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.AbstractIntegrationTest;
import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MentorProfileRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User mentorUser;
    private MentorProfile mentorProfile;
    private TechnicalSkill javaSkill;

    @BeforeEach
    void setUp() {
        mentorUser = new User();
        mentorUser.setName("Mentor Test");
        mentorUser.setEmail("mentor_repo@test.com");
        mentorUser.setPassword("Pass123!");
        mentorUser.setRoles(List.of("MENTOR"));
        entityManager.persist(mentorUser);

        javaSkill = new TechnicalSkill();
        javaSkill.setName("Java");
        entityManager.persist(javaSkill);

        mentorProfile = new MentorProfile();
        mentorProfile.setUser(mentorUser);
        mentorProfile.setBio("Bio Test");
        mentorProfile.setSkills(List.of(javaSkill));
        entityManager.persist(mentorProfile);

        entityManager.flush();
    }

    @Test
    void shouldReturnProfileWhenUserIdExists() {
        Optional<MentorProfile> found = mentorProfileRepository.findByUserId(mentorUser.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getBio()).isEqualTo("Bio Test");
    }

    @Test
    void shouldReturnEmptyWhenUserIdHasNoProfile() {
        Optional<MentorProfile> found = mentorProfileRepository.findByUserId(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnProfileWhenEmailExists() {
        Optional<MentorProfile> found = mentorProfileRepository.findByUserEmail("mentor_repo@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getName()).isEqualTo("Mentor Test");
    }

    @Test
    void shouldReturnMentorsWhenSkillNameMatchesIgnoringCase() {
        List<MentorProfile> found = mentorProfileRepository.findBySkills(List.of("java"));

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUser().getName()).isEqualTo("Mentor Test");
    }

    @Test
    void shouldReturnMentorsWhenSkillNameIsUpperCase() {
        List<MentorProfile> found = mentorProfileRepository.findBySkills(List.of("JAVA"));

        assertThat(found).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSkillDoesNotExist() {
        List<MentorProfile> found = mentorProfileRepository.findBySkills(List.of("cobol"));

        assertThat(found).isEmpty();
    }
}