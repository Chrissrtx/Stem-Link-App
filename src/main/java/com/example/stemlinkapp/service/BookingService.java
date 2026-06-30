package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.*;
import com.example.stemlinkapp.dto.BookingRequest;
import com.example.stemlinkapp.dto.BookingResponse;
import com.example.stemlinkapp.dto.BookingStatusRequest;
import com.example.stemlinkapp.event.MentorshipSessionCreatedEvent;
import com.example.stemlinkapp.exception.InvalidOperationException;
import com.example.stemlinkapp.exception.ResourceNotFoundException;
import com.example.stemlinkapp.exception.TimeSlotConflictException;
import com.example.stemlinkapp.exception.UnauthorizedException;
import com.example.stemlinkapp.repository.AvailabilityBlockRepository;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.UserRepository;
import com.example.stemlinkapp.repository.MentorshipSessionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final MentorshipSessionRepository mentorshipSessionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;

    public BookingService(
            BookingRepository bookingRepository,
            AvailabilityBlockRepository availabilityBlockRepository,
            MentorProfileRepository mentorProfileRepository,
            UserRepository userRepository,
            MentorshipSessionRepository mentorshipSessionRepository,
            ApplicationEventPublisher eventPublisher,
            EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.userRepository = userRepository;
        this.mentorshipSessionRepository = mentorshipSessionRepository;
        this.eventPublisher = eventPublisher;
        this.emailService = emailService;
    }

    @Transactional
    public BookingResponse createBooking(String studentEmail, BookingRequest request) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + studentEmail));

        MentorProfile mentor = mentorProfileRepository.findById(request.getMentorProfileId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mentor no encontrado con id: " + request.getMentorProfileId()));

        if (mentor.getUser().getEmail().equals(studentEmail)) {
            throw new InvalidOperationException("No puedes reservar una sesión contigo mismo");
        }

        AvailabilityBlock block = resolveAvailabilityBlock(mentor, request);

        boolean conflict = bookingRepository.existsConflictingBooking(
                mentor.getId(), request.getDate(), block.getStartTime(), block.getEndTime());

        if (conflict) {
            throw new TimeSlotConflictException("El mentor ya tiene una reserva en ese horario");
        }

        Booking booking = new Booking();
        booking.setStudent(student);
        booking.setMentor(mentor);
        booking.setDate(request.getDate());
        booking.setStartTime(block.getStartTime());
        booking.setEndTime(block.getEndTime());
        booking.setTopic(normalizeTopic(request.getTopic(), mentor));
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(java.time.LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);
        eventPublisher.publishEvent(new MentorshipSessionCreatedEvent(this, saved));
        return toResponse(saved);
    }

    @Transactional
    public BookingResponse updateStatus(String userEmail, Long bookingId, BookingStatusRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id: " + bookingId));

        String mentorEmail = booking.getMentor().getUser().getEmail();
        String studentEmail = booking.getStudent().getEmail();

        switch (request.getStatus()) {
            case CONFIRMED -> {
                if (!userEmail.equals(mentorEmail))
                    throw new UnauthorizedException("Solo el mentor puede confirmar una reserva");
                if (booking.getStatus() != BookingStatus.PENDING)
                    throw new InvalidOperationException("Solo se pueden confirmar reservas en estado PENDING");

                java.util.Map<String, Object> vars = new java.util.HashMap<>();
                vars.put("userName", booking.getStudent().getName());
                vars.put("mentorName", booking.getMentor().getUser().getName());
                vars.put("date", booking.getDate().toString());
                vars.put("startTime", booking.getStartTime().toString());
                vars.put("endTime", booking.getEndTime().toString());
                emailService.sendHtmlMessage(studentEmail, "¡Reserva Confirmada! - STEM Link", "booking-confirmed.html", vars);

                // Crear la sesión real de mentoría
                MentorshipSession session = new MentorshipSession();
                session.setBooking(booking);
                session.setTopic(booking.getTopic());
                session.setNotes("Sesión programada");
                mentorshipSessionRepository.save(session);
            }
            case CANCELLED -> {
                if (!userEmail.equals(mentorEmail) && !userEmail.equals(studentEmail))
                    throw new UnauthorizedException("Solo el mentor o estudiante pueden cancelar");
                if (booking.getStatus() == BookingStatus.CANCELLED)
                    throw new InvalidOperationException("La reserva ya está cancelada");

                String recipientEmail = userEmail.equals(mentorEmail) ? studentEmail : mentorEmail;
                java.util.Map<String, Object> vars = new java.util.HashMap<>();
                vars.put("date", booking.getDate().toString());
                vars.put("startTime", booking.getStartTime().toString());
                emailService.sendHtmlMessage(recipientEmail, "Sesión Cancelada - STEM Link", "booking-cancelled.html", vars);
            }
            default -> throw new InvalidOperationException("Cambio de estado no permitido: " + request.getStatus());
        }

        booking.setStatus(request.getStatus());
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookings(String userEmail, String status, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userEmail));

        BookingStatus bookingStatus = parseStatus(status);
        boolean mentorView = user.getRoles() != null && user.getRoles().contains("MENTOR");

        Page<Booking> bookings = mentorView
                ? bookingRepository.findMentorBookings(userEmail, bookingStatus, pageable)
                : bookingRepository.findStudentBookings(userEmail, bookingStatus, pageable);

        return bookings.map(this::toResponse);
    }

    private BookingStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return BookingStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException error) {
            throw new InvalidOperationException("Estado inválido: " + status);
        }
    }

    private AvailabilityBlock resolveAvailabilityBlock(MentorProfile mentor, BookingRequest request) {
        if (request.getAvailabilityBlockId() != null) {
            AvailabilityBlock block = availabilityBlockRepository.findById(request.getAvailabilityBlockId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bloque no encontrado con id: " + request.getAvailabilityBlockId()));

            validateBlockBelongsToMentorAndDate(mentor, request.getDate().getDayOfWeek(), block);
            return block;
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new InvalidOperationException("Debes indicar availabilityBlockId o un horario válido");
        }

        DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();
        return availabilityBlockRepository.findByMentorProfileIdAndDayOfWeekAndStartTimeAndEndTime(
                        mentor.getId(), dayOfWeek, request.getStartTime(), request.getEndTime())
                .map(block -> {
                    validateBlockBelongsToMentorAndDate(mentor, dayOfWeek, block);
                    return block;
                })
                .orElseThrow(() -> new ResourceNotFoundException("No existe un bloque disponible para ese horario"));
    }

    private void validateBlockBelongsToMentorAndDate(MentorProfile mentor, DayOfWeek requestedDay, AvailabilityBlock block) {
        if (!block.getMentorProfile().getId().equals(mentor.getId())) {
            throw new InvalidOperationException("El bloque no pertenece al mentor indicado");
        }

        if (!requestedDay.equals(block.getDayOfWeek())) {
            throw new InvalidOperationException("La fecha no corresponde al día del bloque: " + block.getDayOfWeek());
        }
    }

    private String normalizeTopic(String topic, MentorProfile mentor) {
        if (topic != null && !topic.isBlank()) {
            return topic.trim();
        }
        return "Sesión de mentoría STEM con " + mentor.getUser().getName();
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse r = new BookingResponse();
        r.setId(booking.getId());
        r.setDate(booking.getDate());
        r.setStartTime(booking.getStartTime());
        r.setEndTime(booking.getEndTime());
        r.setTopic(booking.getTopic());
        r.setStatus(booking.getStatus().name());
        r.setStudentName(booking.getStudent().getName());
        r.setStudentEmail(booking.getStudent().getEmail());
        r.setMentorName(booking.getMentor().getUser().getName());
        r.setMentorEmail(booking.getMentor().getUser().getEmail());
        return r;
    }
}
