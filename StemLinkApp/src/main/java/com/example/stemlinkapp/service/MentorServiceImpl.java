package com.example.stemlinkapp.service;

import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.dto.TechnicalSkillDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MentorServiceImpl implements MentorService {

    // Mock data for demonstration purposes
    private final List<MentorProfileResponse> mockMentors = new ArrayList<>(Arrays.asList(
            new MentorProfileResponse() {{
                setId(1L);
                setName("Alice Smith");
                setBio("Experienced software engineer with a passion for mentoring.");
                setSocialMediaLink("linkedin.com/in/alicesmith");
                setSkills(Arrays.asList(
                        new TechnicalSkillDTO() {{ setId(1L); setName("Java"); }},
                        new TechnicalSkillDTO() {{ setId(2L); setName("Spring Boot"); }}
                ));
            }},
            new MentorProfileResponse() {{
                setId(2L);
                setName("Bob Johnson");
                setBio("Data scientist specializing in machine learning.");
                setSocialMediaLink("github.com/bobj");
                setSkills(Arrays.asList(
                        new TechnicalSkillDTO() {{ setId(3L); setName("Python"); }},
                        new TechnicalSkillDTO() {{ setId(4L); setName("Machine Learning"); }}
                ));
            }}
    ));

    // In a real application, this would interact with a repository
    private MentorProfileResponse findMentorById(Long id) {
        return mockMentors.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public MentorProfileResponse updateMentorProfile(Long userId, MentorProfileRequest request) {
        // In a real app, fetch mentor entity by userId, update, save, and then map to DTO
        MentorProfileResponse mentor = findMentorById(userId);
        if (mentor != null) {
            mentor.setBio(request.getBio());
            mentor.setSocialMediaLink(request.getSocialMediaLink());
            // For mock data, we just return the updated mock object
            return mentor;
        }
        // Handle not found case, e.g., throw an exception
        return null;
    }

    @Override
    public MentorProfileResponse associateSkillsToMentor(Long userId, List<Long> skillIds) {
        // In a real app, fetch mentor entity, fetch skill entities, associate, save, and map
        MentorProfileResponse mentor = findMentorById(userId);
        if (mentor != null) {
            // Mocking skill association
            List<TechnicalSkillDTO> newSkills = skillIds.stream()
                    .map(id -> {
                        // In a real app, fetch skill from TechnicalSkillService/Repository
                        return new TechnicalSkillDTO() {{ setId(id); setName("Skill " + id); }};
                    })
                    .collect(Collectors.toList());
            mentor.setSkills(newSkills);
            return mentor;
        }
        return null;
    }

    @Override
    public List<MentorProfileResponse> filterMentors(String name, List<Long> skillIds) {
        return mockMentors.stream()
                .filter(mentor -> {
                    boolean nameMatches = (name == null || name.isEmpty()) ||
                                         mentor.getName().toLowerCase().contains(name.toLowerCase());
                    boolean skillsMatch = (skillIds == null || skillIds.isEmpty()) ||
                                          mentor.getSkills().stream()
                                                .anyMatch(skill -> skillIds.contains(skill.getId()));
                    return nameMatches && skillsMatch;
                })
                .collect(Collectors.toList());
    }

    @Override
    public MentorProfileResponse getMentorProfile(Long id) {
        return findMentorById(id);
    }
}
