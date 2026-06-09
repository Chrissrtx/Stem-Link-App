package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.AbstractIntegrationTest;
import com.example.stemlinkapp.domain.TechnicalSkill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalSkillRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private TechnicalSkillRepository technicalSkillRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistSkillWhenSaved() {
        TechnicalSkill skill = new TechnicalSkill();
        skill.setName("Python");

        TechnicalSkill saved = technicalSkillRepository.save(skill);

        TechnicalSkill found = entityManager.find(TechnicalSkill.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Python");
    }

    @Test
    void shouldReturnAllSkillsWhenMultipleSaved() {
        TechnicalSkill skill1 = new TechnicalSkill();
        skill1.setName("Java");
        TechnicalSkill skill2 = new TechnicalSkill();
        skill2.setName("Kotlin");

        entityManager.persist(skill1);
        entityManager.persist(skill2);
        entityManager.flush();

        List<TechnicalSkill> all = technicalSkillRepository.findAll();

        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldDeleteSkillWhenRemoved() {
        TechnicalSkill skill = new TechnicalSkill();
        skill.setName("COBOL");
        TechnicalSkill saved = technicalSkillRepository.save(skill);

        technicalSkillRepository.deleteById(saved.getId());

        TechnicalSkill found = entityManager.find(TechnicalSkill.class, saved.getId());
        assertThat(found).isNull();
    }
}