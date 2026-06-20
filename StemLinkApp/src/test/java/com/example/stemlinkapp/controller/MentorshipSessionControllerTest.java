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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorshipSessionController.class)
@AutoConfigureMockMvc
public class MentorshipSessionControllerTest {

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
    void whenGetMySessions_thenReturnOk() throws Exception {
        when(mentorshipSessionService.getSessionHistory(anyString(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/sessions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "student@test.com")
    void whenLeaveFeedback_thenReturnNoContent() throws Exception {
        SessionFeedbackDTO feedbackDTO = new SessionFeedbackDTO();
        feedbackDTO.setStars(5);
        feedbackDTO.setMentorComments("Excellent!");

        doNothing().when(mentorshipSessionService).submitFeedback(eq(1L), any(SessionFeedbackDTO.class), anyString());

        mockMvc.perform(post("/api/sessions/1/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenLeaveFeedbackUnauthenticated_thenUnauthorized() throws Exception {
        mockMvc.perform(post("/api/sessions/1/feedback")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
