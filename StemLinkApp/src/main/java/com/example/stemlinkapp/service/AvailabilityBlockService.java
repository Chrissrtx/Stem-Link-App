package com.example.stemlinkapp.service;

import com.example.stemlinkapp.repository.AvailabilityBlockRepository;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityBlockService {
    private final AvailabilityBlockRepository availabilityBlockRepository;

    public AvailabilityBlockService(AvailabilityBlockRepository availabilityBlockRepository) {
        this.availabilityBlockRepository = availabilityBlockRepository;
    }
}
