package com.attendance.backend.model.academics.repository;

import com.attendance.backend.model.academics.AcademicSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicSessionRepository
        extends JpaRepository<AcademicSession, Long> {

    // =========================================
    // FIND BY DISPLAY NAME (e.g. 2023-2027)
    // =========================================
    Optional<AcademicSession> findByDisplayName(String displayName);

    // =========================================
    // DUPLICATE PROTECTION
    // =========================================
    boolean existsByStartYearAndEndYear(
            Integer startYear,
            Integer endYear
    );
}
