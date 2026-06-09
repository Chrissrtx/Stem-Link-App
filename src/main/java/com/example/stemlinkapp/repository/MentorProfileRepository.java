package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {

    Optional<MentorProfile> findByUserId(Long userId);

    Optional<MentorProfile> findByUserEmail(String email);

    @Query("""
        SELECT DISTINCT m FROM MentorProfile m 
        JOIN m.skills s 
        WHERE LOWER(s.name) IN :skillNames
    """)
    List<MentorProfile> findBySkills(@Param("skillNames") List<String> skillNames);
}
