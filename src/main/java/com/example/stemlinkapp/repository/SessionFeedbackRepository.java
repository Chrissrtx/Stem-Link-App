package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.SessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionFeedbackRepository extends JpaRepository<SessionFeedback, Long> {
}
