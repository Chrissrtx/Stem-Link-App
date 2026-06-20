package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.MentorshipSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {
}
