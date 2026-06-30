package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.BookingRequest;
import com.example.stemlinkapp.dto.BookingResponse;
import com.example.stemlinkapp.dto.BookingStatusRequest;
import com.example.stemlinkapp.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping({"/api/v1/bookings", "/api/bookings"})
    public ResponseEntity<Page<BookingResponse>> getBookings(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10) Pageable pageable,
            Principal principal) {
        return ResponseEntity.ok(bookingService.getBookings(principal.getName(), status, pageable));
    }

    @PostMapping({"/api/v1/bookings", "/api/bookings", "/api/sessions"})
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(principal.getName(), request));
    }

    @PatchMapping({"/api/v1/bookings/{id}/status", "/api/bookings/{id}/status", "/api/sessions/{id}/status"})
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusRequest request,
            Principal principal) {
        return ResponseEntity.ok(bookingService.updateStatus(principal.getName(), id, request));
    }
}
