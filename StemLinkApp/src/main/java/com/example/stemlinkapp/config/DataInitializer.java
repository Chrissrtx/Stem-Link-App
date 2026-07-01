package com.example.stemlinkapp.config;

import com.example.stemlinkapp.domain.*;
import com.example.stemlinkapp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepo,
            MentorProfileRepository mentorProfileRepo,
            TechnicalSkillRepository skillRepo,
            BookingRepository bookingRepo,
            MentorshipSessionRepository sessionRepo,
            PasswordEncoder encoder
        ) {
        return args -> {
            if (userRepo.count() > 0) return;

            // Skills
            TechnicalSkill java   = skill(skillRepo, "Java", "Programación orientada a objetos con Java");
            TechnicalSkill python = skill(skillRepo, "Python", "Ciencia de datos e IA con Python");
            TechnicalSkill web    = skill(skillRepo, "Desarrollo Web", "HTML, CSS, JavaScript, React");
            TechnicalSkill ml     = skill(skillRepo, "Machine Learning", "Modelos predictivos y redes neuronales");
            TechnicalSkill db     = skill(skillRepo, "Bases de Datos", "SQL, PostgreSQL, modelado relacional");

            // Mentores
            User mentorUser1 = createUser(userRepo, encoder, "Carlos Mendoza", "carlos.mentor@stemlink.com", "Mentor@123", "MENTOR");
            MentorProfile mentor1 = createMentorProfile(mentorProfileRepo, mentorUser1,
                    "Ingeniero de software con 8 años de experiencia en backend Java y arquitecturas cloud.",
                    "https://linkedin.com/in/carlosmendoza", "https://meet.google.com/demo-carlos",
                    "15 estudiantes mentoreados", List.of(java, db));

            User mentorUser2 = createUser(userRepo, encoder, "Ana Torres", "ana.mentor@stemlink.com", "Mentor@123", "MENTOR");
            MentorProfile mentor2 = createMentorProfile(mentorProfileRepo, mentorUser2,
                    "Data Scientist en fintech. Especialista en Python, ML y visualización de datos.",
                    "https://linkedin.com/in/anatorres", "https://meet.google.com/demo-ana",
                    "23 estudiantes mentoreados", List.of(python, ml, web));

            User mentorUser3 = createUser(userRepo, encoder, "Diego Ramos", "diego.mentor@stemlink.com", "Mentor@123", "MENTOR");
            MentorProfile mentor3 = createMentorProfile(mentorProfileRepo, mentorUser3,
                    "Desarrollador fullstack con experiencia en React, Node.js y AWS. Co-fundador de dos startups.",
                    "https://linkedin.com/in/diegoramos", "https://meet.google.com/demo-diego",
                    "9 estudiantes mentoreados", List.of(web, java, db));

            // Estudiantes
            User student1 = createUser(userRepo, encoder, "Lucia Paredes", "lucia.student@stemlink.com", "Student@123", "STUDENT");
            User student2 = createUser(userRepo, encoder, "Miguel Castro", "miguel.student@stemlink.com", "Student@123", "STUDENT");

            // Bookings
            Booking b1 = createBooking(bookingRepo, student1, mentor1, BookingStatus.CONFIRMED,
                    LocalDate.now().plusDays(3), LocalTime.of(10, 0), LocalTime.of(11, 0));

            Booking b2 = createBooking(bookingRepo, student2, mentor2, BookingStatus.PENDING,
                    LocalDate.now().plusDays(5), LocalTime.of(15, 0), LocalTime.of(16, 0));

            Booking b3 = createBooking(bookingRepo, student1, mentor3, BookingStatus.CONFIRMED,
                    LocalDate.now().plusDays(7), LocalTime.of(9, 0), LocalTime.of(10, 0));

            // Sesiones para bookings confirmados
            createSession(sessionRepo, b1, "Introducción a Spring Boot y arquitectura REST");
            createSession(sessionRepo, b3, "React desde cero: componentes, hooks y estado");
        };
    }

    private TechnicalSkill skill(TechnicalSkillRepository repo, String name, String desc) {
        TechnicalSkill s = new TechnicalSkill();
        s.setName(name);
        s.setDescription(desc);
        return repo.save(s);
    }

    private User createUser(UserRepository repo, PasswordEncoder encoder,
                            String name, String email, String password, String role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRoles(List.of(role));
        return repo.save(u);
    }

    private MentorProfile createMentorProfile(MentorProfileRepository repo, User user,
                                               String bio, String linkedin, String videoCall,
                                               String impactMetrics, List<TechnicalSkill> skills) {
        MentorProfile p = new MentorProfile();
        p.setUser(user);
        p.setBio(bio);
        p.setLinkedinUrl(linkedin);
        p.setVideoCallUrl(videoCall);
        p.setImpactMetrics(impactMetrics);
        p.setSkills(skills);
        return repo.save(p);
    }

    private Booking createBooking(BookingRepository repo, User student, MentorProfile mentor,
                                  BookingStatus status, LocalDate date, LocalTime start, LocalTime end) {
        Booking b = new Booking();
        b.setStudent(student);
        b.setMentor(mentor);
        b.setStatus(status);
        b.setDate(date);
        b.setStartTime(start);
        b.setEndTime(end);
        b.setCreatedAt(LocalDateTime.now());
        return repo.save(b);
    }

    private void createSession(MentorshipSessionRepository repo, Booking booking, String topic) {
        MentorshipSession s = new MentorshipSession();
        s.setBooking(booking);
        s.setTopic(topic);
        repo.save(s);
    }
}
