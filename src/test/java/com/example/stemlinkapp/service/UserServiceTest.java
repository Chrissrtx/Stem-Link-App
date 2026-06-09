package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.AuthResponse;
import com.example.stemlinkapp.dto.LoginRequest;
import com.example.stemlinkapp.dto.RegisterRequest;
import com.example.stemlinkapp.dto.UserResponse;
import com.example.stemlinkapp.exception.EmailAlreadyExistsException;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.UserRepository;
import com.example.stemlinkapp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private MentorProfileRepository mentorProfileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private ModelMapper modelMapper;
    @Mock private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User savedUser;
    private RegisterRequest studentRequest;
    private RegisterRequest mentorRequest;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Test User");
        savedUser.setEmail("test@test.com");
        savedUser.setRoles(List.of("STUDENT"));

        studentRequest = new RegisterRequest();
        studentRequest.setName("Student Name");
        studentRequest.setEmail("student@test.com");
        studentRequest.setPassword("SecurePass123!");
        studentRequest.setRole("STUDENT");

        mentorRequest = new RegisterRequest();
        mentorRequest.setName("Mentor Name");
        mentorRequest.setEmail("mentor@test.com");
        mentorRequest.setPassword("SecurePass123!");
        mentorRequest.setRole("MENTOR");
    }

    @Test
    void shouldRegisterStudentWhenEmailIsNew() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(new UserResponse());

        UserResponse response = userService.register(studentRequest);

        assertThat(response).isNotNull();
        verify(userRepository).save(any(User.class));
        verify(mentorProfileRepository, never()).save(any(MentorProfile.class));
        verify(emailService).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldCreateMentorProfileWhenRegisteringMentor() {
        savedUser.setRoles(List.of("MENTOR"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(new UserResponse());

        userService.register(mentorRequest);

        verify(mentorProfileRepository).save(any(MentorProfile.class));
        verify(emailService).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));

        assertThatThrownBy(() -> userService.register(studentRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsInvalid() {
        studentRequest.setRole("ADMIN");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.register(studentRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnTokenWhenLoginCredentialsAreValid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("pass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = userService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any());
    }
}