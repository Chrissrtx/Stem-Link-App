package com.example.stemlinkapp.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequest {

    @NotNull(message = "El ID del mentor es obligatorio")
    private Long mentorProfileId;

    private Long availabilityBlockId;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha debe ser futura")
    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;
    private String topic;
}
