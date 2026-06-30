package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import com.example.stemlinkapp.service.TechnicalSkillService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/technical-skills", "/api/technical-skills"})
public class TechnicalSkillController {

    private final TechnicalSkillService technicalSkillService;

    public TechnicalSkillController(TechnicalSkillService technicalSkillService) {
        this.technicalSkillService = technicalSkillService;
    }

    @GetMapping
    public ResponseEntity<List<TechnicalSkillDTO>> getAllTechnicalSkills() {
        return ResponseEntity.ok(technicalSkillService.getAllTechnicalSkills());
    }
}
