package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.service.SessionFeedbackService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackSesionController {
    private final SessionFeedbackService sessionFeedbackService;

    public FeedbackSesionController(SessionFeedbackService sessionFeedbackService) {
        this.sessionFeedbackService = sessionFeedbackService;
    }
}
