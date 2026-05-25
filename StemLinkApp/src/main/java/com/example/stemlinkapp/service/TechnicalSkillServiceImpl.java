package com.example.stemlinkapp.service;

import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TechnicalSkillServiceImpl implements TechnicalSkillService {

    private final List<TechnicalSkillDTO> mockSkills = Arrays.asList(
            new TechnicalSkillDTO() {{ setId(1L); setName("Java"); }},
            new TechnicalSkillDTO() {{ setId(2L); setName("Spring Boot"); }},
            new TechnicalSkillDTO() {{ setId(3L); setName("Python"); }},
            new TechnicalSkillDTO() {{ setId(4L); setName("Machine Learning"); }},
            new TechnicalSkillDTO() {{ setId(5L); setName("Data Science"); }},
            new TechnicalSkillDTO() {{ setId(6L); setName("Web Development"); }},
            new TechnicalSkillDTO() {{ setId(7L); setName("Mobile Development"); }}
    );

    @Override
    public List<TechnicalSkillDTO> getAllTechnicalSkills() {
        return mockSkills;
    }
}
