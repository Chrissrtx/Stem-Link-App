package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import com.example.stemlinkapp.service.MentorService;
import com.example.stemlinkapp.service.TechnicalSkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class MentorController {

    private final MentorService mentorService;
    private final TechnicalSkillService technicalSkillService;

    public MentorController(MentorService mentorService, TechnicalSkillService technicalSkillService) {
        this.mentorService = mentorService;
        this.technicalSkillService = technicalSkillService;
    }

    @PatchMapping("/users/me/profile")
    public ResponseEntity<MentorProfileResponse> updateMentorProfile(@RequestBody MentorProfileRequest request, Principal principal) {
        MentorProfileResponse response = mentorService.updateMentorProfile(principal.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/me/tags")
    public ResponseEntity<MentorProfileResponse> associateTechnicalSkills(@RequestBody List<Long> skillIds, Principal principal) {
        MentorProfileResponse response = mentorService.associateSkillsToMentor(principal.getName(), skillIds);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mentors")
    public ResponseEntity<Page<MentorProfileResponse>> listMentors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Long> skillIds,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<MentorProfileResponse> mentors = mentorService.filterMentors(name, skillIds, pageable);
        return ResponseEntity.ok(mentors);
    }

    @GetMapping("/mentors/{id}")
    public ResponseEntity<MentorProfileResponse> getMentorProfile(@PathVariable Long id) {
        MentorProfileResponse mentor = mentorService.getMentorProfile(id);
        return ResponseEntity.ok(mentor);
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TechnicalSkillDTO>> getAllTechnicalSkills() {
        List<TechnicalSkillDTO> skills = technicalSkillService.getAllTechnicalSkills();
        return ResponseEntity.ok(skills);
    }
}