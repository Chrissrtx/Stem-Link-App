package com.example.stemlinkapp.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
    private String status;
    private String studentName;
    private String studentEmail;
    private String mentorName;
    private String mentorEmail;
}
