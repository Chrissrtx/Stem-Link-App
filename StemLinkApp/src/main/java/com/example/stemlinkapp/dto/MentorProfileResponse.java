package com.example.stemlinkapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class MentorProfileResponse {
    private Long id;
    private String name;
    private String bio;
    private String videoCallUrl;
    private String linkedinUrl;
    private List<TechnicalSkillDTO> skills;
}
