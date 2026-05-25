package com.example.stemlinkapp.service;

import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import java.util.List;

public interface MentorService {
    MentorProfileResponse updateMentorProfile(String email, MentorProfileRequest request);
    MentorProfileResponse associateSkillsToMentor(String email, List<Long> skillIds);
    List<MentorProfileResponse> filterMentors(String name, List<Long> skillIds);
    MentorProfileResponse getMentorProfile(Long id);
}
