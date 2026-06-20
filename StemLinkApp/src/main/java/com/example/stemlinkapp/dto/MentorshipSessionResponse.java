package com.example.stemlinkapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MentorshipSessionResponse {
    private Long id;
    private String topic;
    private String status;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mentorName;
    private String studentName;
}
