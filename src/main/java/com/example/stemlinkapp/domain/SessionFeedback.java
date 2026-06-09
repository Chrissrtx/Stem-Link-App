package com.example.stemlinkapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SessionFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer stars;
    private String mentorComments;
    private String impactRecord;

    @OneToOne
    @JoinColumn(name = "mentorship_session_id")
    private MentorshipSession mentorshipSession;
}
