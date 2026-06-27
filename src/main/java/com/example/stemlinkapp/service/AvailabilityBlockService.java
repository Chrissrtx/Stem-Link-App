package com.example.stemlinkapp.service;

import com.example.stemlinkapp.domain.AvailabilityBlock;
import com.example.stemlinkapp.domain.MentorProfile;
import com.example.stemlinkapp.dto.AvailabilityBlockDTO;
import com.example.stemlinkapp.exception.InvalidOperationException;
import com.example.stemlinkapp.exception.ResourceNotFoundException;
import com.example.stemlinkapp.exception.TimeSlotConflictException;
import com.example.stemlinkapp.exception.UnauthorizedException;
import com.example.stemlinkapp.repository.AvailabilityBlockRepository;
import com.example.stemlinkapp.repository.BookingRepository;
import com.example.stemlinkapp.repository.MentorProfileRepository;
import com.example.stemlinkapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvailabilityBlockService {

    private final AvailabilityBlockRepository availabilityBlockRepository;
    private final BookingRepository bookingRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AvailabilityBlockService(
            AvailabilityBlockRepository availabilityBlockRepository,
            BookingRepository bookingRepository,
            MentorProfileRepository mentorProfileRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.availabilityBlockRepository = availabilityBlockRepository;
        this.bookingRepository = bookingRepository;
        this.mentorProfileRepository = mentorProfileRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityBlockDTO> getBlocksByMentor(Long mentorProfileId) {
        MentorProfile mentor = mentorProfileRepository.findById(mentorProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Perfil de mentor no encontrado con id: " + mentorProfileId));
        return availabilityBlockRepository.findByMentorProfile(mentor)
                .stream()
                .map(block -> modelMapper.map(block, AvailabilityBlockDTO.class))
                .toList();
    }

    @Transactional
    public AvailabilityBlockDTO createBlock(String email, AvailabilityBlockDTO dto) {
        MentorProfile mentor = getMentorProfileByEmail(email);

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new InvalidOperationException("La hora de inicio debe ser anterior a la hora de fin");
        }

        boolean overlaps = availabilityBlockRepository.existsOverlap(
                mentor.getId(), dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime(), 0L);

        if (overlaps) {
            throw new TimeSlotConflictException("Ya existe un bloque que se superpone con ese horario");
        }

        AvailabilityBlock block = new AvailabilityBlock();
        block.setDayOfWeek(dto.getDayOfWeek());
        block.setStartTime(dto.getStartTime());
        block.setEndTime(dto.getEndTime());
        block.setMentorProfile(mentor);

        return modelMapper.map(availabilityBlockRepository.save(block), AvailabilityBlockDTO.class);
    }

    @Transactional
    public void deleteBlock(String email, Long blockId) {
        MentorProfile mentor = getMentorProfileByEmail(email);

        AvailabilityBlock block = availabilityBlockRepository.findById(blockId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bloque no encontrado con id: " + blockId));

        if (!block.getMentorProfile().getId().equals(mentor.getId())) {
            throw new UnauthorizedException("No tienes permiso para eliminar este bloque");
        }

        List<com.example.stemlinkapp.domain.Booking> activeBookings = bookingRepository.findActiveBookingsOverlappingTime(
                mentor.getId(), block.getStartTime(), block.getEndTime());
        boolean hasActive = activeBookings.stream()
                .anyMatch(b -> b.getDate().getDayOfWeek().equals(block.getDayOfWeek()));
        if (hasActive) {
            throw new InvalidOperationException("No se puede eliminar un bloque con reservas activas");
        }

        availabilityBlockRepository.delete(block);
    }

    private MentorProfile getMentorProfileByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + email));
        if (user.getMentorProfile() == null) {
            throw new InvalidOperationException("El usuario no tiene perfil de mentor");
        }
        return user.getMentorProfile();
    }
}