package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.domain.TechnicalSkill;
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
public class MentorProfileRepositoryTest {

    @Autowired
    private MentorProfileRepository mentorProfileRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User mentorUser;
    private MentorProfile mentorProfile;
    private TechnicalSkill skill;

    @BeforeEach
    void setUp() {
        mentorUser = new User();
        mentorUser.setName("Mentor Test");
        mentorUser.setEmail("mentor_repo@test.com");
        mentorUser.setPassword("Pass123!");
        mentorUser.setRoles(List.of("MENTOR"));
        entityManager.persist(mentorUser);

        skill = new TechnicalSkill();
        skill.setName("Java");
        entityManager.persist(skill);

        mentorProfile = new MentorProfile();
        mentorProfile.setUser(mentorUser);
        mentorProfile.setBio("Bio Test");
        mentorProfile.setSkills(List.of(skill));
        entityManager.persist(mentorProfile);
        
        entityManager.flush();
    }

    @Test
    void whenFindByUserId_thenReturnProfile() {
        Optional<MentorProfile> found = mentorProfileRepository.findByUserId(mentorUser.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBio()).isEqualTo("Bio Test");
    }

    @Test
    void whenFindBySkills_thenReturnMentors() {
        List<MentorProfile> found = mentorProfileRepository.findBySkills(List.of("java"));
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getUser().getName()).isEqualTo("Mentor Test");
    }
}
