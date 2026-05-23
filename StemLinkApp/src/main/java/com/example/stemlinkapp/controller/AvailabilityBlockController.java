package com.example.stemlinkapp.controller;

import com.example.stemlinkapp.service.AvailabilityBlockService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityBlockController {
    private final AvailabilityBlockService availabilityBlockService;

    public AvailabilityBlockController(AvailabilityBlockService availabilityBlockService) {
        this.availabilityBlockService = availabilityBlockService;
    }
}
