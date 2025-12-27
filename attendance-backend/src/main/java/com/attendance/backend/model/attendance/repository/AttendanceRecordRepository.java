package com.attendance.backend.model.attendance.repository;

import com.attendance.backend.dto.AttendanceViewDTO;
import com.attendance.backend.model.attendance.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    long countByDate(LocalDate date);

    // =====================================================
    // STUDENT STATUS (LIVE CLASS)
    // =====================================================
    @Query("""
        SELECT ar.status
        FROM AttendanceRecord ar
        WHERE ar.classSession.id = :classId
          AND ar.student.id = :studentId
    """)
    String findStatusForStudentInClass(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    boolean existsByStudentIdAndClassSessionId(
            Long studentId,
            Long classSessionId
    );

    // =====================================================
    // ADMIN — PAGINATED VIEW (POSTGRES SAFE)
    // =====================================================
    @Query(
            value = """
        SELECT
            s.id            AS userId,
            s.roll_no       AS rollNo,
            s.name          AS userName,
            cs.id           AS classId,
            cs.subject_name AS subjectName,
            t.name          AS teacherName,
            al.date         AS date,
            al.timestamp    AS timestamp,
            al.status       AS status
        FROM attendance_logs al
        JOIN students s ON al.student_id = s.id
        JOIN class_sessions cs ON al.class_session_id = cs.id
        JOIN teachers t ON t.id = cs.teacher_id
        WHERE (:date IS NULL OR al.date = :date)
          AND (:subject IS NULL OR cs.subject_name ILIKE CONCAT('%', :subject, '%'))
          AND (:teacher IS NULL OR t.name ILIKE CONCAT('%', :teacher, '%'))
          AND (:student IS NULL OR s.name ILIKE CONCAT('%', :student, '%'))
          AND (:status IS NULL OR al.status = :status)
          AND (:courseId IS NULL OR s.course_id = :courseId)
          AND (:branchId IS NULL OR s.branch_id = :branchId)
          AND (:academicSessionId IS NULL OR s.academic_session_id = :academicSessionId)
          AND (:semesterId IS NULL OR s.semester_id = :semesterId)
        ORDER BY al.timestamp DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM attendance_logs al
        JOIN students s ON al.student_id = s.id
        JOIN class_sessions cs ON al.class_session_id = cs.id
        JOIN teachers t ON t.id = cs.teacher_id
        WHERE (:date IS NULL OR al.date = :date)
          AND (:subject IS NULL OR cs.subject_name ILIKE CONCAT('%', :subject, '%'))
          AND (:teacher IS NULL OR t.name ILIKE CONCAT('%', :teacher, '%'))
          AND (:student IS NULL OR s.name ILIKE CONCAT('%', :student, '%'))
          AND (:status IS NULL OR al.status = :status)
          AND (:courseId IS NULL OR s.course_id = :courseId)
          AND (:branchId IS NULL OR s.branch_id = :branchId)
          AND (:academicSessionId IS NULL OR s.academic_session_id = :academicSessionId)
          AND (:semesterId IS NULL OR s.semester_id = :semesterId)
        """,
            nativeQuery = true
    )
    Page<AttendanceViewDTO> fetchAttendanceViewPaged(
            @Param("date") LocalDate date,
            @Param("subject") String subject,
            @Param("teacher") String teacher,
            @Param("student") String student,
            @Param("status") String status,
            @Param("courseId") Long courseId,
            @Param("branchId") Long branchId,
            @Param("academicSessionId") Long academicSessionId,
            @Param("semesterId") Long semesterId,
            Pageable pageable
    );


    // =====================================================
    // ADMIN — EXCEL EXPORT
    // =====================================================
    @Query(
            value = """
            SELECT
                s.id            AS userId,
                s.roll_no       AS rollNo,
                s.name          AS userName,
                cs.id           AS classId,
                cs.subject_name AS subjectName,
                t.name          AS teacherName,
                al.date         AS date,
                al.timestamp    AS timestamp,
                al.status       AS status
            FROM attendance_logs al
            JOIN students s ON al.student_id = s.id
            JOIN class_sessions cs ON al.class_session_id = cs.id
            JOIN teachers t ON t.id = cs.teacher_id
            WHERE (:date IS NULL OR al.date = CAST(:date AS DATE))
              AND (:subject IS NULL OR cs.subject_name ILIKE CONCAT('%', :subject, '%'))
              AND (:teacher IS NULL OR t.name ILIKE CONCAT('%', :teacher, '%'))
              AND (:student IS NULL OR s.name ILIKE CONCAT('%', :student, '%'))
              AND (:status IS NULL OR al.status = CAST(:status AS VARCHAR))
              AND (:courseId IS NULL OR s.course_id = CAST(:courseId AS BIGINT))
              AND (:branchId IS NULL OR s.branch_id = CAST(:branchId AS BIGINT))
              AND (:academicSessionId IS NULL OR s.academic_session_id = CAST(:academicSessionId AS BIGINT))
              AND (:semesterId IS NULL OR s.semester_id = CAST(:semesterId AS BIGINT))
            ORDER BY al.timestamp DESC
            """,
            nativeQuery = true
    )
    List<AttendanceViewDTO> fetchAttendanceView(
            @Param("date") LocalDate date,
            @Param("subject") String subject,
            @Param("teacher") String teacher,
            @Param("student") String student,
            @Param("status") String status,
            @Param("courseId") Long courseId,
            @Param("branchId") Long branchId,
            @Param("academicSessionId") Long academicSessionId,
            @Param("semesterId") Long semesterId
    );
}
