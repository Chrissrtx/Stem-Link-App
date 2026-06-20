package com.example.stemlinkapp.dto;

import com.example.stemlinkapp.domain.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    private BookingStatus status;
}