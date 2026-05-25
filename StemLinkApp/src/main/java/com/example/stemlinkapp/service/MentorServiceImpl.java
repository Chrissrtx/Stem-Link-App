package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.exception.ResourceNotFoundException;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import com.example.stemlinkapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MentorServiceImpl implements MentorService {

    private final MentorProfileRepository mentorProfileRepository;
    private final TechnicalSkillRepository technicalSkillRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public MentorServiceImpl(MentorProfileRepository mentorProfileRepository,
                             TechnicalSkillRepository technicalSkillRepository,
                             UserRepository userRepository,
                             ModelMapper modelMapper) {
        this.mentorProfileRepository = mentorProfileRepository;
        this.technicalSkillRepository = technicalSkillRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public MentorProfileResponse updateMentorProfile(Long userId, MentorProfileRequest request) {
        MentorProfile mentor = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de mentor no encontrado para el usuario: " + userId));
        
        mentor.setBio(request.getBio());
        // Map other fields as necessary from request
        return modelMapper.map(mentorProfileRepository.save(mentor), MentorProfileResponse.class);
    }

    @Override
    @Transactional
    public MentorProfileResponse associateSkillsToMentor(Long userId, List<Long> skillIds) {
        MentorProfile mentor = mentorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de mentor no encontrado"));
        
        List<TechnicalSkill> skills = technicalSkillRepository.findAllById(skillIds);
        mentor.setSkills(skills);
        
        return modelMapper.map(mentorProfileRepository.save(mentor), MentorProfileResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorProfileResponse> filterMentors(String name, List<Long> skillIds) {
        List<MentorProfile> mentors;
        
        if (skillIds != null && !skillIds.isEmpty()) {
            // Since our repo uses skill names for the custom query, let's adapt or use a name-based filter
            // For now, let's assume we filter by ID or fetch skill names first
            List<String> skillNames = technicalSkillRepository.findAllById(skillIds)
                    .stream().map(TechnicalSkill::getName).map(String::toLowerCase).toList();
            mentors = mentorProfileRepository.findBySkills(skillNames);
        } else {
            mentors = mentorProfileRepository.findAll();
        }

        if (name != null && !name.isEmpty()) {
            mentors = mentors.stream()
                    .filter(m -> m.getUser().getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return mentors.stream()
                .map(m -> modelMapper.map(m, MentorProfileResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MentorProfileResponse getMentorProfile(Long id) {
        MentorProfile mentor = mentorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor no encontrado con ID: " + id));
        return modelMapper.map(mentor, MentorProfileResponse.class);
    }
}
