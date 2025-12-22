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
    // → Future + Live classes (NOT expired, NOT deleted)
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
        WHERE c.deleted = false
          AND c.endTime >= :now
        ORDER BY c.startTime ASC
    """)
    List<ClassSessionDto> findStudentCurrentAndFutureClasses(
            @Param("now") LocalDateTime now
    );

    // =====================================================
    // TEACHER DASHBOARD
    // → Future + Live classes (NOT expired, NOT deleted)
    // =====================================================
    @Query("""
        SELECT c
        FROM ClassSession c
        WHERE c.teacher.id = :teacherId
          AND c.deleted = false
          AND c.endTime >= :now
        ORDER BY c.startTime ASC
    """)
    List<ClassSession> findTeacherCurrentAndFutureClasses(
            @Param("teacherId") Long teacherId,
            @Param("now") LocalDateTime now
    );

    // =====================================================
    // TIME OVERLAP CHECK (SAME TEACHER ONLY)
    // → Must consider future + live classes, ignore deleted
    // =====================================================
    @Query("""
        SELECT COUNT(c) > 0
        FROM ClassSession c
        WHERE c.teacher.id = :teacherId
          AND c.deleted = false
          AND c.startTime < :endTime
          AND c.endTime > :startTime
    """)
    boolean existsOverlappingClassForTeacher(
            @Param("teacherId") Long teacherId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // =====================================================
    // ADMIN DASHBOARD
    // =====================================================
    List<ClassSession> findByActiveTrueAndDeletedFalse();

    List<ClassSession> findByDeletedTrue();

    @Query("""
        SELECT COUNT(c)
        FROM ClassSession c
        WHERE c.active = true
          AND c.deleted = false
    """)
    long countActiveClasses();

    // =====================================================
    // LIFECYCLE MANAGEMENT (SCHEDULER)
    // =====================================================

    // Auto-activate future classes (ignore deleted)
    @Query("""
        SELECT c
        FROM ClassSession c
        WHERE c.deleted = false
          AND c.active = false
          AND c.startTime <= :now
          AND c.endTime > :now
    """)
    List<ClassSession> findClassesToActivate(
            @Param("now") LocalDateTime now
    );

    // Auto-expire finished classes (ignore deleted)
    @Query("""
        SELECT c
        FROM ClassSession c
        WHERE c.deleted = false
          AND c.active = true
          AND c.endTime < :now
    """)
    List<ClassSession> findClassesToExpire(
            @Param("now") LocalDateTime now
    );
}
