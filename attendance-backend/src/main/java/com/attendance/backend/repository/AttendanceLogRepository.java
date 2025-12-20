package com.attendance.backend.repository;

import com.attendance.backend.dto.AttendanceViewDTO;
import com.attendance.backend.entity.AttendanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceLogRepository
        extends JpaRepository<AttendanceLog, Long> {

    // =============================
    // DASHBOARD COUNT (SAFE)
    // =============================
    long countByDate(LocalDate date);

    // =============================
    // ✅ PREVENT DUPLICATE ATTENDANCE
    // (PER STUDENT PER CLASS)
    // =============================
    boolean existsByUserIdAndClassId(
            Long userId,
            Long classId
    );

    // =============================
    // ADMIN / TEACHER REPORT
    // =============================
    @Query("""
        SELECT new com.attendance.backend.dto.AttendanceViewDTO(
            u.id,
            u.rollNo,
            u.name,
            cs.id,
            cs.subjectName,
            t.name,
            al.date,
            al.timestamp,
            al.status
        )
        FROM AttendanceLog al
        JOIN User u ON al.userId = u.id
        JOIN ClassSession cs ON al.classId = cs.id
        JOIN cs.teacher t
        ORDER BY al.timestamp DESC
    """)
    List<AttendanceViewDTO> fetchAttendanceReport();
}
