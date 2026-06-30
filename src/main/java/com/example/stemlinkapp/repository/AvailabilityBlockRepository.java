package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.AvailabilityBlock;
import com.example.stemlinkapp.domain.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AvailabilityBlockRepository extends JpaRepository<AvailabilityBlock, Long> {

    List<AvailabilityBlock> findByMentorProfile(MentorProfile mentorProfile);

    List<AvailabilityBlock> findByMentorProfileId(Long mentorProfileId);

    Optional<AvailabilityBlock> findByMentorProfileIdAndDayOfWeekAndStartTimeAndEndTime(
            Long mentorProfileId,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    );

    @Query("""
            SELECT COUNT(a) > 0 FROM AvailabilityBlock a
            WHERE a.mentorProfile.id = :mentorId
              AND a.dayOfWeek = :day
              AND a.id <> :excludeId
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlap(
            @Param("mentorId") Long mentorId,
            @Param("day") DayOfWeek day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );
}
