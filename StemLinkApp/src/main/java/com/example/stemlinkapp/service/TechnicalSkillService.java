package com.example.stemlinkapp.service;

import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import org.springframework.stereotype.Service;

@Service
public class TechnicalSkillService {
    private final TechnicalSkillRepository technicalSkillRepository;

    public TechnicalSkillService(TechnicalSkillRepository technicalSkillRepository) {
        this.technicalSkillRepository = technicalSkillRepository;
    }
}
