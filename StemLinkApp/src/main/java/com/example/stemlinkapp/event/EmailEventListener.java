package com.example.stemlinkapp.event;

import com.example.stemlinkapp.domain.Booking;
import com.example.stemlinkapp.service.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailEventListener {

    private final EmailService emailService;

    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleMentorshipSessionCreated(MentorshipSessionCreatedEvent event) {
        Booking booking = event.getBooking();
        
        String mentorEmail = booking.getMentor().getUser().getEmail();
        String subject = "Nueva Solicitud de Mentoría - STEM Link";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("mentorName", booking.getMentor().getUser().getName());
        variables.put("studentName", booking.getStudent().getName());
        variables.put("date", booking.getDate().toString());
        variables.put("startTime", booking.getStartTime().toString());
        variables.put("endTime", booking.getEndTime().toString());

        emailService.sendHtmlMessage(mentorEmail, subject, "mentorship-request.html", variables);
    }
}
