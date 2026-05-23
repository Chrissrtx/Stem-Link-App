package com.example.stemlinkapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;
    private String videoCallUrl;
    private String impactMetrics;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
        name = "mentor_skills",
        joinColumns = @JoinColumn(name = "mentor_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<TechnicalSkill> skills;

    @OneToMany(mappedBy = "mentorProfile")
    private List<AvailabilityBlock> availability;
}
