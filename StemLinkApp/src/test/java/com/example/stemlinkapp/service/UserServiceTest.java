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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MentorProfileRepository mentorProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private RegisterRequest studentRequest;
    private RegisterRequest mentorRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
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

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@test.com");
        testUser.setRoles(List.of("STUDENT"));
    }

    @Test
    void whenRegisterStudent_thenSuccess() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(new UserResponse());

        // When
        UserResponse response = userService.register(studentRequest);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(mentorProfileRepository, never()).save(any(MentorProfile.class));
        verify(emailService, times(1)).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void whenRegisterMentor_thenCreateProfileAndSuccess() {
        // Given
        testUser.setRoles(List.of("MENTOR"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(modelMapper.map(any(User.class), eq(UserResponse.class))).thenReturn(new UserResponse());

        // When
        UserResponse response = userService.register(mentorRequest);

        // Then
        assertThat(response).isNotNull();
        verify(userRepository, times(1)).save(any(User.class));
        verify(mentorProfileRepository, times(1)).save(any(MentorProfile.class));
        verify(emailService, times(1)).sendHtmlMessage(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void whenRegisterDuplicateEmail_thenThrowException() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> userService.register(studentRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenLogin_thenReturnAuthResponse() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("pass");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        // When
        AuthResponse response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("token");
        verify(authenticationManager, times(1)).authenticate(any());
    }
}
