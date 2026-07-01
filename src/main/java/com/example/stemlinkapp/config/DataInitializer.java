package com.example.stemlinkapp.config;

import com.example.stemlinkapp.domain.AvailabilityBlock;
import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.domain.BookingStatus;
import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.domain.MentorshipSession;
import com.example.stemlinkapp.domain.Notification;
import com.example.stemlinkapp.domain.TechnicalSkill;
import com.example.stemlinkapp.domain.User;
import com.example.stemlinkapp.repository.AvailabilityBlockRepository;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import com.example.stemlinkapp.repository.NotificationRepository;
import com.example.stemlinkapp.repository.TechnicalSkillRepository;
import com.example.stemlinkapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepository,
            MentorProfileRepository mentorProfileRepository,
            TechnicalSkillRepository technicalSkillRepository,
            AvailabilityBlockRepository availabilityBlockRepository,
            BookingRepository bookingRepository,
            MentorshipSessionRepository mentorshipSessionRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder
        ) {
        return args -> {
            TechnicalSkill java = ensureSkill(technicalSkillRepository, "Java", "Programación orientada a objetos con Java y Spring.");
            TechnicalSkill python = ensureSkill(technicalSkillRepository, "Python", "Automatización, ciencia de datos e IA.");
            TechnicalSkill react = ensureSkill(technicalSkillRepository, "React", "Interfaces modernas con componentes y hooks.");
            TechnicalSkill databases = ensureSkill(technicalSkillRepository, "Bases de Datos", "SQL, PostgreSQL y modelado relacional.");
            TechnicalSkill ml = ensureSkill(technicalSkillRepository, "Machine Learning", "Modelos predictivos y fundamentos de IA.");
            TechnicalSkill cloud = ensureSkill(technicalSkillRepository, "Cloud", "Despliegue y arquitectura en la nube.");

            User mentorCarlos = ensureUser(userRepository, passwordEncoder, "Carlos Mendoza", "carlos.mentor@stemlink.com", "Mentor@123", "MENTOR");
            User mentorAna = ensureUser(userRepository, passwordEncoder, "Ana Torres", "ana.mentor@stemlink.com", "Mentor@123", "MENTOR");
            User mentorDiego = ensureUser(userRepository, passwordEncoder, "Diego Ramos", "diego.mentor@stemlink.com", "Mentor@123", "MENTOR");
            User studentLucia = ensureUser(userRepository, passwordEncoder, "Lucia Student", "lucia.student@stemlink.com", "Student@123", "STUDENT");
            User studentMiguel = ensureUser(userRepository, passwordEncoder, "Miguel Student", "miguel.student@stemlink.com", "Student@123", "STUDENT");

            MentorProfile carlosProfile = ensureMentorProfile(
                    mentorProfileRepository,
                    mentorCarlos,
                    "Ingeniero backend con foco en Java, APIs REST y despliegue cloud.",
                    "https://linkedin.com/in/carlosmendoza",
                    "https://meet.google.com/demo-carlos",
                    "15 estudiantes mentoreados",
                    List.of(java, databases, cloud)
            );
            MentorProfile anaProfile = ensureMentorProfile(
                    mentorProfileRepository,
                    mentorAna,
                    "Data scientist en fintech. Especialista en Python, ML y storytelling con datos.",
                    "https://linkedin.com/in/anatorres",
                    "https://meet.google.com/demo-ana",
                    "23 estudiantes mentoreados",
                    List.of(python, ml, databases)
            );
            MentorProfile diegoProfile = ensureMentorProfile(
                    mentorProfileRepository,
                    mentorDiego,
                    "Fullstack engineer con experiencia en React, DX y productos SaaS.",
                    "https://linkedin.com/in/diegoramos",
                    "https://meet.google.com/demo-diego",
                    "9 estudiantes mentoreados",
                    List.of(react, java, cloud)
            );

            if (bookingRepository.count() == 0) {
                AvailabilityBlock carlosBlockOne = createAvailability(availabilityBlockRepository, carlosProfile, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0));
                AvailabilityBlock carlosBlockTwo = createAvailability(availabilityBlockRepository, carlosProfile, DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(17, 0));
                AvailabilityBlock anaBlockOne = createAvailability(availabilityBlockRepository, anaProfile, DayOfWeek.TUESDAY, LocalTime.of(15, 0), LocalTime.of(16, 0));
                AvailabilityBlock diegoBlockOne = createAvailability(availabilityBlockRepository, diegoProfile, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(10, 0));

                Booking bookingOne = createBooking(
                        bookingRepository, studentLucia, carlosProfile, BookingStatus.CONFIRMED,
                        nextDate(DayOfWeek.MONDAY), carlosBlockOne.getStartTime(), carlosBlockOne.getEndTime(),
                        "Introducción a Spring Boot"
                );
                Booking bookingThree = createBooking(
                        bookingRepository, studentLucia, diegoProfile, BookingStatus.CONFIRMED,
                        nextDate(DayOfWeek.FRIDAY), diegoBlockOne.getStartTime(), diegoBlockOne.getEndTime(),
                        "React desde cero"
                );
                createBooking(
                        bookingRepository, studentMiguel, anaProfile, BookingStatus.PENDING,
                        nextDate(DayOfWeek.TUESDAY), anaBlockOne.getStartTime(), anaBlockOne.getEndTime(),
                        "Ruta de aprendizaje para Machine Learning"
                );
                createBooking(
                        bookingRepository, studentMiguel, carlosProfile, BookingStatus.CANCELLED,
                        nextDate(DayOfWeek.WEDNESDAY), carlosBlockTwo.getStartTime(), carlosBlockTwo.getEndTime(),
                        "Diseño de APIs REST"
                );

                createSession(mentorshipSessionRepository, bookingOne, bookingOne.getTopic());
                createSession(mentorshipSessionRepository, bookingThree, bookingThree.getTopic());

                createNotification(notificationRepository, mentorCarlos, "Nueva solicitud", "Lucia Student ha solicitado una sesión contigo.", false);
                createNotification(notificationRepository, mentorAna, "Reserva pendiente", "Miguel Student está esperando tu respuesta.", false);
                createNotification(notificationRepository, studentLucia, "Reserva confirmada", "Carlos Mendoza confirmó tu sesión de Spring Boot.", true);
                createNotification(notificationRepository, studentMiguel, "Reserva cancelada", "Una de tus reservas fue cancelada y requiere reprogramación.", false);
            }
        };
    }

    private TechnicalSkill ensureSkill(TechnicalSkillRepository repository, String name, String description) {
        return repository.findAll().stream()
                .filter(skill -> skill.getName() != null && skill.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> createSkill(repository, name, description));
    }

    private TechnicalSkill createSkill(TechnicalSkillRepository repository, String name, String description) {
        TechnicalSkill skill = new TechnicalSkill();
        skill.setName(name);
        skill.setDescription(description);
        return repository.save(skill);
    }

    private User ensureUser(UserRepository repository, PasswordEncoder passwordEncoder, String name, String email, String password, String role) {
        return repository.findByEmail(email)
                .orElseGet(() -> createUser(repository, passwordEncoder, name, email, password, role));
    }

    private User createUser(UserRepository repository, PasswordEncoder passwordEncoder, String name, String email, String password, String role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(List.of(role));
        return repository.save(user);
    }

    private MentorProfile ensureMentorProfile(
            MentorProfileRepository repository,
            User user,
            String bio,
            String linkedinUrl,
            String videoCallUrl,
            String impactMetrics,
            List<TechnicalSkill> skills
    ) {
        return repository.findByUserEmail(user.getEmail())
                .orElseGet(() -> createMentorProfile(repository, user, bio, linkedinUrl, videoCallUrl, impactMetrics, skills));
    }

    private MentorProfile createMentorProfile(
            MentorProfileRepository repository,
            User user,
            String bio,
            String linkedinUrl,
            String videoCallUrl,
            String impactMetrics,
            List<TechnicalSkill> skills
    ) {
        MentorProfile profile = new MentorProfile();
        profile.setUser(user);
        profile.setBio(bio);
        profile.setLinkedinUrl(linkedinUrl);
        profile.setVideoCallUrl(videoCallUrl);
        profile.setImpactMetrics(impactMetrics);
        profile.setSkills(skills);
        return repository.save(profile);
    }

    private AvailabilityBlock createAvailability(
            AvailabilityBlockRepository repository,
            MentorProfile mentorProfile,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {
        AvailabilityBlock block = new AvailabilityBlock();
        block.setMentorProfile(mentorProfile);
        block.setDayOfWeek(dayOfWeek);
        block.setStartTime(startTime);
        block.setEndTime(endTime);
        return repository.save(block);
    }

    private Booking createBooking(
            BookingRepository repository,
            User student,
            MentorProfile mentor,
            BookingStatus status,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            String topic
    ) {
        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setMentor(mentor);
        booking.setStatus(status);
        booking.setDate(date);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setTopic(topic);
        booking.setCreatedAt(LocalDateTime.now());
        return repository.save(booking);
    }

    private void createSession(MentorshipSessionRepository repository, Booking booking, String topic) {
        MentorshipSession session = new MentorshipSession();
        session.setBooking(booking);
        session.setTopic(topic);
        session.setNotes("Sesión generada como dato demo");
        repository.save(session);
    }

    private void createNotification(NotificationRepository repository, User user, String title, String message, boolean read) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setRead(read);
        notification.setCreatedAt(LocalDateTime.now().minusHours(read ? 12 : 2));
        repository.save(notification);
    }

    private LocalDate nextDate(DayOfWeek dayOfWeek) {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() != dayOfWeek) {
            date = date.plusDays(1);
        }
        return date;
    }
}
