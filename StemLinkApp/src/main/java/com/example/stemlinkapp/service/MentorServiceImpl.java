package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.exception.ResourceNotFoundException;
import com.example.stemlinkapp.exception.SkillNotFoundException;
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
    public MentorProfileResponse updateMentorProfile(String email, MentorProfileRequest request) {
        MentorProfile mentor = mentorProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de mentor no encontrado para el usuario: " + email));
        
        mentor.setBio(request.getBio());
        mentor.setVideoCallUrl(request.getVideoCallUrl());
        mentor.setLinkedinUrl(request.getLinkedinUrl());
        
        return modelMapper.map(mentorProfileRepository.save(mentor), MentorProfileResponse.class);
    }

    @Override
    @Transactional
    public MentorProfileResponse associateSkillsToMentor(String email, List<Long> skillIds) {
        MentorProfile mentor = mentorProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de mentor no encontrado para el usuario: " + email));
        
        List<TechnicalSkill> skills = technicalSkillRepository.findAllById(skillIds);
        
        if (skills.size() != skillIds.size()) {
            throw new SkillNotFoundException("Una o más habilidades no existen en el catálogo");
        }
        
        mentor.setSkills(skills);
        return modelMapper.map(mentorProfileRepository.save(mentor), MentorProfileResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorProfileResponse> filterMentors(String name, List<Long> skillIds) {
        List<MentorProfile> mentors;
        
        if (skillIds != null && !skillIds.isEmpty()) {
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
