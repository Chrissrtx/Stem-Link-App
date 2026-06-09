package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnicalSkillServiceImpl implements TechnicalSkillService {

    private final TechnicalSkillRepository technicalSkillRepository;
    private final ModelMapper modelMapper;

    public TechnicalSkillServiceImpl(TechnicalSkillRepository technicalSkillRepository, ModelMapper modelMapper) {
        this.technicalSkillRepository = technicalSkillRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TechnicalSkillDTO> getAllTechnicalSkills() {
        return technicalSkillRepository.findAll().stream()
                .map(skill -> modelMapper.map(skill, TechnicalSkillDTO.class))
                .collect(Collectors.toList());
    }
}
