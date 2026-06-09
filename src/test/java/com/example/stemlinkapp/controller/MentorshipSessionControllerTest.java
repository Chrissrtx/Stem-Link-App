package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.SessionFeedbackDTO;
import com.example.stemlinkapp.security.JwtService;
import com.example.stemlinkapp.service.MentorshipSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipSessionController.class)
@AutoConfigureMockMvc
class MentorshipSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentorshipSessionService mentorshipSessionService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "student@test.com")
    void shouldReturnOkWhenAuthenticatedUserRequestsSessions() throws Exception {
        when(mentorshipSessionService.getSessionHistory(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/mentorship-sessions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student@test.com")
    void shouldReturnNoContentWhenFeedbackIsSubmitted() throws Exception {
        SessionFeedbackDTO feedbackDTO = new SessionFeedbackDTO();
        feedbackDTO.setStars(5);
        feedbackDTO.setMentorComments("Excellent!");

        doNothing().when(mentorshipSessionService).submitFeedback(eq(1L), any(), anyString());

        mockMvc.perform(post("/api/v1/mentorship-sessions/1/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/mentorship-sessions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenSubmittingFeedbackWithoutAuth() throws Exception {
        SessionFeedbackDTO feedbackDTO = new SessionFeedbackDTO();
        feedbackDTO.setStars(4);

        mockMvc.perform(post("/api/v1/mentorship-sessions/1/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isUnauthorized());
    }
}