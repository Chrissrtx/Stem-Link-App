package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.dto.AuthResponse;
import com.example.stemlinkapp.dto.LoginRequest;
import com.example.stemlinkapp.dto.RegisterRequest;
import com.example.stemlinkapp.dto.UserResponse;
import com.example.stemlinkapp.exception.EmailAlreadyExistsException;
import com.example.stemlinkapp.exception.UserNotFoundException;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.UserRepository;
import com.example.stemlinkapp.security.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "MENTOR");

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final EmailService emailService;

    public UserService(
            UserRepository userRepository,
            MentorProfileRepository mentorProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            ModelMapper modelMapper,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }

        String role = normalizeRole(request.getRole());

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(role));

        User savedUser = userRepository.save(user);

        if ("MENTOR".equals(role)) {
            MentorProfile mentorProfile = new MentorProfile();
            mentorProfile.setUser(savedUser);
            mentorProfile.setBio("¡Hola! Soy un nuevo mentor en STEM Link.");
            mentorProfile.setImpactMetrics("Nuevas sesiones por realizar");
            mentorProfileRepository.save(mentorProfile);
        }

        java.util.Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("userName", savedUser.getName());
        emailService.sendHtmlMessage(savedUser.getEmail(), "¡Bienvenido a STEM Link!", "welcome.html", variables);

        return toUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(getPrimaryRole(user))
                .build();
    }

    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new IllegalStateException("No hay usuario autenticado");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.setRole(getPrimaryRole(user));
        return response;
    }

    private String getPrimaryRole(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return null;
        }

        return user.getRoles().get(0);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizeRole(String role) {
        String normalizedRole = role.trim().toUpperCase();

        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new IllegalArgumentException("Rol inválido. Roles permitidos: STUDENT, MENTOR");
        }

        return normalizedRole;
    }
}
