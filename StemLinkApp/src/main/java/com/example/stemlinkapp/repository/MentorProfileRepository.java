package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
}
