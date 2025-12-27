package com.attendance.backend.model.routine.repository;

import com.attendance.backend.dto.ClassSessionDto;
import com.attendance.backend.dto.TeacherClassSessionDto;
import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.user.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {

    // =====================================================
    // STUDENT → ASSIGNED CLASSES (LIVE + UPCOMING)
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
        WHERE c.course = :course
          AND c.branch = :branch
          AND c.academicSession = :academicSession
          AND c.deleted = false
          AND c.endTime >= :now
        ORDER BY c.startTime ASC
    """)
    List<ClassSessionDto> findStudentClassesByRoutine(
            @Param("course") Course course,
            @Param("branch") Branch branch,
            @Param("academicSession") AcademicSession academicSession,
            @Param("now") LocalDateTime now
    );


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
    WHERE c.course = :course
      AND c.branch = :branch
      AND c.academicSession = :academicSession
      AND c.sessionDate = :today
      AND c.deleted = false
    ORDER BY c.startTime ASC
""")
    List<ClassSessionDto> findStudentClassesForToday(
            @Param("course") Course course,
            @Param("branch") Branch branch,
            @Param("academicSession") AcademicSession academicSession,
            @Param("today") LocalDate today
    );

    // =====================================================
    // TEACHER → TODAY'S CLASSES (DTO ONLY – SAFE)
    // =====================================================
    @Query("""
        SELECT new com.attendance.backend.dto.TeacherClassSessionDto(
            c.id,
            c.subjectName,
            c.course.name,
            c.branch.name,
            c.startTime,
            c.endTime,
            c.active
        )
        FROM ClassSession c
        WHERE c.teacher.id = :teacherId
          AND c.deleted = false
          AND c.startTime >= :startOfDay
          AND c.startTime < :endOfDay
        ORDER BY c.startTime ASC
    """)
    List<TeacherClassSessionDto> findTeacherClassesForDate(
            @Param("teacherId") Long teacherId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // =====================================================
    // DUPLICATE PREVENTION (GENERATION SAFETY)
    // =====================================================
    boolean existsByTeacherAndStartTime(
            Teacher teacher,
            LocalDateTime startTime
    );

    boolean existsByCourseAndBranchAndAcademicSessionAndStartTime(
            Course course,
            Branch branch,
            AcademicSession academicSession,
            LocalDateTime startTime
    );

    // =====================================================
    // ADMIN → OVERLAPPING SAFETY
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
    // SCHEDULER → AUTO ACTIVATE / EXPIRE
    // =====================================================
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

    // =====================================================
    // OPTIONAL → SIMPLE DAILY FETCH (INTERNAL USE)
    // =====================================================
    List<ClassSession> findByTeacherIdAndSessionDate(
            Long teacherId,
            LocalDate sessionDate
    );

    void deleteBySessionDate(LocalDate sessionDate);

}
