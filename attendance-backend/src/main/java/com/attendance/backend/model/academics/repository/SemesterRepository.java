package com.attendance.backend.model.academics.repository;

import com.attendance.backend.model.academics.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // =====================================================
    // ACTIVE SEMESTER (USED IN ROUTINE / CLASSES)
    // =====================================================
    Optional<Semester> findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
            Long courseId,
            Long branchId,
            Long academicSessionId
    );

    // =====================================================
    // VALIDATION (ADMIN SIDE)
    // =====================================================
    boolean existsByCourseIdAndBranchIdAndAcademicSessionIdAndNumber(
            Long courseId,
            Long branchId,
            Long academicSessionId,
            int number
    );

    // =====================================================
    // ALL SEMESTERS (ADMIN FILTER / DROPDOWN)
    // =====================================================
    List<Semester> findByCourseIdAndBranchIdAndAcademicSessionId(
            Long courseId,
            Long branchId,
            Long academicSessionId
    );
}
