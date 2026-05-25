package com.example.stemlinkapp.service;

import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import java.util.List;

public interface MentorService {
    MentorProfileResponse updateMentorProfile(Long userId, MentorProfileRequest request);
    MentorProfileResponse associateSkillsToMentor(Long userId, List<Long> skillIds);
    List<MentorProfileResponse> filterMentors(String name, List<Long> skillIds);
    MentorProfileResponse getMentorProfile(Long id);
}
