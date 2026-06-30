package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.stemlinkapp.domain.BookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime dateTime);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.mentor.id = :mentorId
              AND b.status IN ('PENDING', 'CONFIRMED')
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    List<Booking> findActiveBookingsOverlappingTime(
            @Param("mentorId") Long mentorId,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.mentor.id = :mentorId
              AND b.date = :date
              AND b.status <> 'CANCELLED'
              AND b.startTime < :endTime
              AND b.endTime > :startTime
            """)
    boolean existsConflictingBooking(
            @Param("mentorId") Long mentorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    List<Booking> findByStudentEmailOrderByDateDesc(String email);

    List<Booking> findByMentorUserEmailOrderByDateDesc(String email);

    @Query("""
            SELECT b FROM Booking b
            WHERE b.student.email = :email
              AND (:status IS NULL OR b.status = :status)
            ORDER BY b.date DESC, b.startTime DESC, b.createdAt DESC
            """)
    Page<Booking> findStudentBookings(
            @Param("email") String email,
            @Param("status") BookingStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT b FROM Booking b
            WHERE b.mentor.user.email = :email
              AND (:status IS NULL OR b.status = :status)
            ORDER BY b.date DESC, b.startTime DESC, b.createdAt DESC
            """)
    Page<Booking> findMentorBookings(
            @Param("email") String email,
            @Param("status") BookingStatus status,
            Pageable pageable
    );
}
