package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.service.TechnicalSkillService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TechnicalSkillController {
    private final TechnicalSkillService technicalSkillService;

    public TechnicalSkillController(TechnicalSkillService technicalSkillService) {
        this.technicalSkillService = technicalSkillService;
    }
}
