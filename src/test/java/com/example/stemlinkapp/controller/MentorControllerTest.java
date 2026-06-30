package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.security.JwtService;
import com.example.stemlinkapp.service.MentorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.stemlinkapp.service.TechnicalSkillService;

@WebMvcTest(MentorController.class)
@AutoConfigureMockMvc
class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TechnicalSkillService technicalSkillService;

    @MockBean
    private MentorService mentorService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldReturnOkWhenListMentors() throws Exception {
        when(mentorService.filterMentors(any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/mentors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser
    void shouldReturnMentorWhenIdExists() throws Exception {
        MentorProfileResponse response = new MentorProfileResponse();
        when(mentorService.getMentorProfile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/mentors/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldReturnOkWhenFilterByName() throws Exception {
        when(mentorService.filterMentors(any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/mentors").param("name", "Java"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnOkWhenNoAuthentication() throws Exception {
        when(mentorService.filterMentors(any(), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/mentors"))
                .andExpect(status().isOk());
    }
}
