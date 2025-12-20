package com.attendance.backend.repository;

import com.attendance.backend.dto.ClassSessionDto;
import com.attendance.backend.entity.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    // =====================================================
    // STUDENT DASHBOARD
    // → Only ACTIVE + CURRENT classes
    // =====================================================
    @Query("""
        SELECT new com.attendance.backend.dto.ClassSessionDto(
            c.id,
            c.subjectName,
            t.name,
            c.latitude,
            c.longitude,
            c.radius
        )
        FROM ClassSession c
        JOIN c.teacher t
        WHERE c.active = true
          AND :now BETWEEN c.startTime AND c.endTime
    """)
    List<ClassSessionDto> findActiveClassesForStudents(
            @Param("now") LocalDateTime now
    );

    // =====================================================
    // TEACHER DASHBOARD
    // → Only ACTIVE classes created by teacher
    // =====================================================
    List<ClassSession> findByTeacher_IdAndActiveTrue(Long teacherId);

    // =====================================================
    // ADMIN (OPTIONAL)
    // → All classes (active + inactive)
    // =====================================================
    List<ClassSession> findAll();

    // =====================================================
    // ADMIN (OPTIONAL)
    // → Only ACTIVE classes
    // =====================================================
    List<ClassSession> findByActiveTrue();
}
