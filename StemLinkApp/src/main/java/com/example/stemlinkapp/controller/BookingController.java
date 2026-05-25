package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.BookingRequest;
import com.example.stemlinkapp.dto.BookingResponse;
import com.example.stemlinkapp.dto.BookingStatusRequest;
import com.example.stemlinkapp.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/sessions")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(principal.getName(), request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusRequest request,
            Principal principal) {
        return ResponseEntity.ok(bookingService.updateStatus(principal.getName(), id, request));
    }
}