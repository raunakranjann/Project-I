package com.attendance.backend.repository;

import com.attendance.backend.dto.AttendanceViewDTO;
import com.attendance.backend.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLogRepository
        extends JpaRepository<AttendanceLog, Long> {

    long countByDate(LocalDate date);

    boolean existsByUserIdAndClassId(Long userId, Long classId);

    // =====================================================
    // ADMIN / TEACHER ATTENDANCE VIEW
    // (POSTGRES + HIBERNATE 6 – GUARANTEED SAFE)
    // =====================================================
    @Query(value = """
        SELECT
            u.id            AS userId,
            u.roll_no       AS rollNo,
            u.name          AS userName,
            cs.id           AS classId,
            cs.subject_name AS subjectName,
            t.name          AS teacherName,
            al.date         AS date,
            al.timestamp    AS timestamp,
            al.status       AS status
        FROM attendance_logs al
        JOIN users u ON al.user_id = u.id
        JOIN class_sessions cs ON al.class_id = cs.id
        JOIN teachers t ON t.id = cs.teacher_id
        WHERE al.date = COALESCE(:date, al.date)
          AND cs.subject_name ILIKE '%' || COALESCE(:subject, cs.subject_name) || '%'
          AND t.name ILIKE '%' || COALESCE(:teacher, t.name) || '%'
          AND al.status = COALESCE(:status, al.status)
        ORDER BY al.timestamp DESC
        """, nativeQuery = true)
    List<AttendanceViewDTO> fetchAttendanceView(
            @Param("date") LocalDate date,
            @Param("subject") String subject,
            @Param("teacher") String teacher,
            @Param("status") String status
    );
}
