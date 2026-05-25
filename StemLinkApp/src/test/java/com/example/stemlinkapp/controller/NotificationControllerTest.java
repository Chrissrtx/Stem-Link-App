package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.security.JwtService;
import com.example.stemlinkapp.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(username = "noti@test.com")
    void whenGetMyNotifications_thenReturnOk() throws Exception {
        when(notificationService.getMyNotifications(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "noti@test.com")
    void whenMarkAsRead_thenReturnNoContent() throws Exception {
        doNothing().when(notificationService).markAsRead(anyLong(), anyString());

        mockMvc.perform(patch("/api/notifications/1/read")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
