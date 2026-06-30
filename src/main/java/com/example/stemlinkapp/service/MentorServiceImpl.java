package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.exception.ResourceNotFoundException;
import com.example.stemlinkapp.exception.SkillNotFoundException;
import com.example.stemlinkapp.exception.UserNotFoundException;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import com.example.stemlinkapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    private MentorProfileResponse toResponse(MentorProfile mentor) {
        MentorProfileResponse response = modelMapper.map(mentor, MentorProfileResponse.class);
        if (mentor.getUser() != null) {
            response.setName(mentor.getUser().getName());
        }
        return response;
    }

    private MentorProfile getOrCreateMentorProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        boolean isMentor = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(role -> "MENTOR".equalsIgnoreCase(role));

        if (!isMentor) {
            throw new IllegalStateException("El usuario no tiene perfil de mentor");
        }

        return mentorProfileRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    MentorProfile mentor = new MentorProfile();
                    mentor.setUser(user);
                    mentor.setBio("¡Hola! Soy un mentor en STEM Link.");
                    mentor.setImpactMetrics("Perfil de mentor recién creado");
                    return mentorProfileRepository.save(mentor);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public MentorProfileResponse getCurrentMentorProfile(String email) {
        MentorProfile mentor = getOrCreateMentorProfile(email);
        return toResponse(mentor);
    }

    @Override
    @Transactional
    public MentorProfileResponse updateMentorProfile(String email, MentorProfileRequest request) {
        MentorProfile mentor = getOrCreateMentorProfile(email);
        
        mentor.setBio(request.getBio());
        mentor.setVideoCallUrl(request.getVideoCallUrl());
        mentor.setLinkedinUrl(request.getLinkedinUrl());
        
        return toResponse(mentorProfileRepository.save(mentor));
    }

    @Override
    @Transactional
    public MentorProfileResponse associateSkillsToMentor(String email, List<Long> skillIds) {
        MentorProfile mentor = getOrCreateMentorProfile(email);
        
        List<TechnicalSkill> skills = technicalSkillRepository.findAllById(skillIds);
        
        if (skills.size() != skillIds.size()) {
            throw new SkillNotFoundException("Una o más habilidades no existen en el catálogo");
        }
        
        mentor.setSkills(skills);
        return toResponse(mentorProfileRepository.save(mentor));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MentorProfileResponse> filterMentors(String name, List<Long> skillIds, Pageable pageable) {
        boolean filterByName = name != null && !name.isBlank();
        boolean filterBySkills = skillIds != null && !skillIds.isEmpty();

        List<String> skillNames = filterBySkills
                ? technicalSkillRepository.findAllById(skillIds).stream()
                        .map(TechnicalSkill::getName)
                        .map(String::toLowerCase)
                        .toList()
                : List.of("");

        String nameFilter = filterByName ? name : "";

        Page<MentorProfile> page = mentorProfileRepository
                .searchMentors(filterByName, nameFilter, filterBySkills, skillNames, pageable);

        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorProfileResponse getMentorProfile(Long id) {
        MentorProfile mentor = mentorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor no encontrado con ID: " + id));
        return toResponse(mentor);
    }
}
