package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import com.example.stemlinkapp.security.JwtService;
import com.example.stemlinkapp.service.MentorService;
import com.example.stemlinkapp.service.TechnicalSkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorController.class)
@AutoConfigureMockMvc
public class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MentorService mentorService;

    @MockBean
    private TechnicalSkillService technicalSkillService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    void whenListMentors_thenReturnOk() throws Exception {
        when(mentorService.filterMentors(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/mentors"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void whenGetTags_thenReturnOk() throws Exception {
        when(technicalSkillService.getAllTechnicalSkills()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MENTOR")
    void whenGetMentorProfile_thenReturnOk() throws Exception {
        when(mentorService.getMentorProfile(1L)).thenReturn(new MentorProfileResponse());

        mockMvc.perform(get("/api/mentors/1"))
                .andExpect(status().isOk());
    }
}
