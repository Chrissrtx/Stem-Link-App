package com.example.stemlinkapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SessionFeedbackDTO {
    @Min(1) @Max(5)
    private Integer stars;
    
    @NotBlank
    private String mentorComments;
    
    private String impactRecord;
}
