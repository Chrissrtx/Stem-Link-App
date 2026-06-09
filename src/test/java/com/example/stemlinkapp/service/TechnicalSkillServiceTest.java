package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicalSkillServiceTest {

    @Mock private TechnicalSkillRepository technicalSkillRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private TechnicalSkillServiceImpl technicalSkillService;

    @Test
    void shouldReturnAllSkillsWhenRepositoryHasSome() {
        TechnicalSkill skill = new TechnicalSkill();
        skill.setName("Java");

        when(technicalSkillRepository.findAll()).thenReturn(List.of(skill));
        when(modelMapper.map(any(), eq(TechnicalSkillDTO.class))).thenReturn(new TechnicalSkillDTO());

        List<TechnicalSkillDTO> result = technicalSkillService.getAllTechnicalSkills();

        assertThat(result).hasSize(1);
        verify(technicalSkillRepository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenRepositoryIsEmpty() {
        when(technicalSkillRepository.findAll()).thenReturn(List.of());

        List<TechnicalSkillDTO> result = technicalSkillService.getAllTechnicalSkills();

        assertThat(result).isEmpty();
    }
}