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
@RequestMapping("/api/sessions")
public class MentorshipSessionController {
    private final MentorshipSessionService mentorshipSessionService;

    public MentorshipSessionController(MentorshipSessionService mentorshipSessionService) {
        this.mentorshipSessionService = mentorshipSessionService;
    }

    @GetMapping
    public ResponseEntity<List<MentorshipSessionResponse>> getMySessions(
            @RequestParam(required = false) String status, 
            Principal principal) {
        return ResponseEntity.ok(mentorshipSessionService.getSessionHistory(principal.getName(), status));
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<Void> leaveFeedback(
            @PathVariable Long id, 
            @Valid @RequestBody SessionFeedbackDTO feedbackDTO) {
        mentorshipSessionService.submitFeedback(id, feedbackDTO);
        return ResponseEntity.noContent().build();
    }
}
