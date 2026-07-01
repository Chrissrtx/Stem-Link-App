package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.MentorProfileRequest;
import com.example.stemlinkapp.dto.MentorProfileResponse;
import com.example.stemlinkapp.exception.SkillNotFoundException;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {

    @Mock
    private MentorProfileRepository mentorProfileRepository;

    @Mock
    private TechnicalSkillRepository technicalSkillRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MentorServiceImpl mentorService;

    private User mentorUser;
    private MentorProfile mentorProfile;
    private String mentorEmail = "mentor@test.com";

    @BeforeEach
    void setUp() {
        mentorUser = new User();
        mentorUser.setId(1L);
        mentorUser.setName("Mentor Test");
        mentorUser.setEmail(mentorEmail);

        mentorProfile = new MentorProfile();
        mentorProfile.setId(1L);
        mentorProfile.setUser(mentorUser);
        mentorProfile.setBio("Original Bio");
    }

    @Test
    void whenUpdateProfile_thenSuccess() {
        MentorProfileRequest request = new MentorProfileRequest();
        request.setBio("Updated Bio");
        request.setVideoCallUrl("http://zoom.us/test");
        
        when(mentorProfileRepository.findByUserEmail(mentorEmail)).thenReturn(Optional.of(mentorProfile));
        when(mentorProfileRepository.save(any(MentorProfile.class))).thenReturn(mentorProfile);
        when(modelMapper.map(any(), eq(MentorProfileResponse.class))).thenReturn(new MentorProfileResponse());

        mentorService.updateMentorProfile(mentorEmail, request);

        assertThat(mentorProfile.getBio()).isEqualTo("Updated Bio");
        assertThat(mentorProfile.getVideoCallUrl()).isEqualTo("http://zoom.us/test");
        verify(mentorProfileRepository, times(1)).save(mentorProfile);
    }

    @Test
    void whenAssociateSkills_thenSuccess() {
        List<Long> skillIds = List.of(1L);
        TechnicalSkill skill = new TechnicalSkill();
        skill.setId(1L);

        when(mentorProfileRepository.findByUserEmail(mentorEmail)).thenReturn(Optional.of(mentorProfile));
        when(technicalSkillRepository.findAllById(skillIds)).thenReturn(List.of(skill));
        when(mentorProfileRepository.save(any(MentorProfile.class))).thenReturn(mentorProfile);
        when(modelMapper.map(any(), eq(MentorProfileResponse.class))).thenReturn(new MentorProfileResponse());

        mentorService.associateSkillsToMentor(mentorEmail, skillIds);

        verify(mentorProfileRepository, times(1)).save(mentorProfile);
    }

    @Test
    void whenAssociateNonExistentSkills_thenThrowException() {
        List<Long> skillIds = List.of(1L, 2L);
        when(mentorProfileRepository.findByUserEmail(mentorEmail)).thenReturn(Optional.of(mentorProfile));
        when(technicalSkillRepository.findAllById(skillIds)).thenReturn(List.of(new TechnicalSkill())); // Only 1 found

        assertThatThrownBy(() -> mentorService.associateSkillsToMentor(mentorEmail, skillIds))
                .isInstanceOf(SkillNotFoundException.class);
    }

    @Test
    void whenFilterMentors_thenSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MentorProfile> mentorPage = new PageImpl<>(List.of(mentorProfile), pageable, 1);

        when(mentorProfileRepository.searchMentors(anyBoolean(), anyString(), anyBoolean(), anyList(), any(Pageable.class)))
                .thenReturn(mentorPage);
        when(modelMapper.map(any(), eq(MentorProfileResponse.class))).thenReturn(new MentorProfileResponse());

        Page<MentorProfileResponse> result = mentorService.filterMentors("Mentor", null, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(mentorProfileRepository, times(1))
                .searchMentors(anyBoolean(), anyString(), anyBoolean(), anyList(), any(Pageable.class));
    }
}
