package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.MentorProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * Búsqueda paginada de mentores con filtros opcionales por nombre y habilidades.
     * - Si name es null no filtra por nombre.
     * - Si filterBySkills es false no filtra por habilidades (skillNames se ignora,
     *   pero debe ser una lista no vacía para que el IN sea SQL válido).
     */
    @Query(value = """
            SELECT m FROM MentorProfile m
            WHERE (:name IS NULL OR LOWER(m.user.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:filterBySkills = false OR EXISTS (
                    SELECT 1 FROM m.skills s WHERE LOWER(s.name) IN :skillNames
              ))
            """,
            countQuery = """
            SELECT COUNT(m) FROM MentorProfile m
            WHERE (:name IS NULL OR LOWER(m.user.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:filterBySkills = false OR EXISTS (
                    SELECT 1 FROM m.skills s WHERE LOWER(s.name) IN :skillNames
              ))
            """)
    Page<MentorProfile> searchMentors(@Param("name") String name,
                                      @Param("filterBySkills") boolean filterBySkills,
                                      @Param("skillNames") List<String> skillNames,
                                      Pageable pageable);
}