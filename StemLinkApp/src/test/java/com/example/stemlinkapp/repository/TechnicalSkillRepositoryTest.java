package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.TechnicalSkill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TechnicalSkillRepositoryTest {

    @Autowired
    private TechnicalSkillRepository technicalSkillRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenSaveSkill_thenPersisted() {
        TechnicalSkill skill = new TechnicalSkill();
        skill.setName("Python");
        
        TechnicalSkill saved = technicalSkillRepository.save(skill);
        
        TechnicalSkill found = entityManager.find(TechnicalSkill.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Python");
    }
}
