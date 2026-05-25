package com.example.stemlinkapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private java.time.LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private MentorProfile mentor;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private MentorshipSession mentorshipSession;
}
