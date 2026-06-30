package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.MentorshipSessionResponse;
import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.service.MentorshipSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class MentorshipSessionController {
    private final MentorshipSessionService mentorshipSessionService;

    public MentorshipSessionController(MentorshipSessionService mentorshipSessionService) {
        this.mentorshipSessionService = mentorshipSessionService;
    }

    @GetMapping({"/api/v1/mentorship-sessions", "/api/mentorship-sessions", "/api/sessions"})
    public ResponseEntity<List<MentorshipSessionResponse>> getMySessions(
            @RequestParam(required = false) String status, 
            Principal principal) {
        return ResponseEntity.ok(mentorshipSessionService.getSessionHistory(principal.getName(), status));
    }

    @PostMapping({"/api/v1/mentorship-sessions/{id}/feedback", "/api/mentorship-sessions/{id}/feedback", "/api/sessions/{id}/feedback"})
    public ResponseEntity<Void> leaveFeedback(
            @PathVariable Long id, 
            @Valid @RequestBody SessionFeedbackDTO feedbackDTO,
            Principal principal) {
        mentorshipSessionService.submitFeedback(id, feedbackDTO, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
