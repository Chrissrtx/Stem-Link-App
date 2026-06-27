package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.BookingStatus;
import com.example.stemlinkapp.domain.MentorshipSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {

    @Query("""
        SELECT m FROM MentorshipSession m
        WHERE (m.booking.student.email = :email OR m.booking.mentor.user.email = :email)
          AND (:status IS NULL OR m.booking.status = :status)
    """)
    List<MentorshipSession> findByUserEmailAndStatus(
            @Param("email") String email,
            @Param("status") BookingStatus status
    );
}
