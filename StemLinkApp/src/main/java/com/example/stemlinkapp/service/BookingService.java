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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public BookingService(
            BookingRepository bookingRepository,
            AvailabilityBlockRepository availabilityBlockRepository,
            MentorProfileRepository mentorProfileRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
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

        AvailabilityBlock block = availabilityBlockRepository.findById(request.getAvailabilityBlockId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bloque no encontrado con id: " + request.getAvailabilityBlockId()));

        if (!block.getMentorProfile().getId().equals(mentor.getId())) {
            throw new InvalidOperationException("El bloque no pertenece al mentor indicado");
        }

        if (!request.getDate().getDayOfWeek().equals(block.getDayOfWeek())) {
            throw new InvalidOperationException(
                    "La fecha no corresponde al día del bloque: " + block.getDayOfWeek());
        }

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
        booking.setStatus(BookingStatus.PENDING);

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
            }
            case CANCELLED -> {
                if (!userEmail.equals(mentorEmail) && !userEmail.equals(studentEmail))
                    throw new UnauthorizedException("Solo el mentor o estudiante pueden cancelar");
                if (booking.getStatus() == BookingStatus.CANCELLED)
                    throw new InvalidOperationException("La reserva ya está cancelada");
            }
            default -> throw new InvalidOperationException("Cambio de estado no permitido: " + request.getStatus());
        }

        booking.setStatus(request.getStatus());
        return toResponse(bookingRepository.save(booking));
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse r = new BookingResponse();
        r.setId(booking.getId());
        r.setDate(booking.getDate());
        r.setStartTime(booking.getStartTime());
        r.setEndTime(booking.getEndTime());
        r.setStatus(booking.getStatus().name());
        r.setStudentName(booking.getStudent().getName());
        r.setStudentEmail(booking.getStudent().getEmail());
        r.setMentorName(booking.getMentor().getUser().getName());
        r.setMentorEmail(booking.getMentor().getUser().getEmail());
        return r;
    }
}