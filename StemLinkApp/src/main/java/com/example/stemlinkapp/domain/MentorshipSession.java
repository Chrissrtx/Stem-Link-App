package com.example.stemlinkapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MentorshipSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topic;
    private String notes;
    private String resourceLinks;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne(mappedBy = "mentorshipSession", cascade = CascadeType.ALL)
    private SessionFeedback feedback;
}
