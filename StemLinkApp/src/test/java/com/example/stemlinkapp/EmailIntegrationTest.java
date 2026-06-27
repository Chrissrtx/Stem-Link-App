package com.example.stemlinkapp;

import com.example.stemlinkapp.domain.*;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import com.example.stemlinkapp.service.EmailService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

@Disabled
@SpringBootTest
public class EmailIntegrationTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    @Test
    void testWelcomeEmailOnRegistration() throws InterruptedException {
        User user = new User();
        user.setName("Nuevo Usuario");
        user.setEmail("bienvenida@test.com");

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", user.getName());

        System.out.println("Enviando email de bienvenida de prueba...");
        emailService.sendHtmlMessage(user.getEmail(), "¡Bienvenido a STEM Link!", "welcome.html", variables);

        Thread.sleep(3000);
        System.out.println("Revisa Mailtrap para el email de bienvenida.");
    }

    @Test
    void testEmailOnBookingConfirmed() throws InterruptedException {
        User student = new User();
        student.setName("Estudiante Feliz");
        student.setEmail("estudiante_confirmado@test.com");

        User mentorUser = new User();
        mentorUser.setName("Mentor Estrella");

        Map<String, Object> vars = new HashMap<>();
        vars.put("userName", student.getName());
        vars.put("mentorName", mentorUser.getName());
        vars.put("date", LocalDate.now().toString());
        vars.put("startTime", "10:00");
        vars.put("endTime", "11:00");

        System.out.println("Enviando email de confirmación de prueba...");
        emailService.sendHtmlMessage(student.getEmail(), "¡Reserva Confirmada! - STEM Link", "booking-confirmed.html", vars);

        Thread.sleep(3000);
        System.out.println("Revisa Mailtrap para el email de confirmación.");
    }

    @Test
    void testEmailOnBookingCancelled() throws InterruptedException {
        String recipientEmail = "notificado_cancelacion@test.com";

        Map<String, Object> vars = new HashMap<>();
        vars.put("date", LocalDate.now().toString());
        vars.put("startTime", "14:00");

        System.out.println("Enviando email de cancelación de prueba...");
        emailService.sendHtmlMessage(recipientEmail, "Sesión Cancelada - STEM Link", "booking-cancelled.html", vars);

        Thread.sleep(3000);
        System.out.println("Revisa Mailtrap para el email de cancelación.");
    }

    @Test
    void testEmailOnBookingCreated() throws InterruptedException {
        User student = new User();
        student.setName("Estudiante de Prueba");
        student.setEmail("estudiante@test.com");

        User mentorUser = new User();
        mentorUser.setName("Mentor de Prueba");
        mentorUser.setEmail("mentor@test.com");

        MentorProfile mentorProfile = new MentorProfile();
        mentorProfile.setUser(mentorUser);

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setMentor(mentorProfile);
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setStartTime(LocalTime.of(15, 0));
        booking.setEndTime(LocalTime.of(16, 0));

        System.out.println("Disparando evento de prueba para Mailtrap...");
        eventPublisher.publishEvent(new MentorshipSessionCreatedEvent(this, booking));

        Thread.sleep(3000);
        System.out.println("Prueba de solicitud finalizada.");
    }
}
