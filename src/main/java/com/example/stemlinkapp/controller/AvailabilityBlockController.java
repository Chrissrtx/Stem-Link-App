package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.dto.AvailabilityBlockDTO;
import com.example.stemlinkapp.service.AvailabilityBlockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class AvailabilityBlockController {

    private final AvailabilityBlockService availabilityBlockService;

    public AvailabilityBlockController(AvailabilityBlockService availabilityBlockService) {
        this.availabilityBlockService = availabilityBlockService;
    }

    @GetMapping({"/api/v1/mentors/{id}/availability", "/api/mentors/{id}/availability"})
    public ResponseEntity<List<AvailabilityBlockDTO>> getAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityBlockService.getBlocksByMentor(id));
    }

    @Secured("ROLE_MENTOR")
    @PostMapping({"/api/v1/mentors/me/availability", "/api/mentors/me/availability"})
    public ResponseEntity<AvailabilityBlockDTO> createBlock(
            @Valid @RequestBody AvailabilityBlockDTO dto,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityBlockService.createBlock(principal.getName(), dto));
    }

    @Secured("ROLE_MENTOR")
    @DeleteMapping({"/api/v1/mentors/me/availability/{id}", "/api/mentors/me/availability/{id}"})
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id, Principal principal) {
        availabilityBlockService.deleteBlock(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
