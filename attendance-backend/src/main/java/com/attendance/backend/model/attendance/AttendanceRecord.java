package com.attendance.backend.model.attendance;

import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.user.Student;
import com.attendance.backend.model.user.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance_logs",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"student_id", "class_session_id"}
        ),
        indexes = {
                @Index(name = "idx_attendance_student", columnList = "student_id"),
                @Index(name = "idx_attendance_class", columnList = "class_session_id"),
                @Index(name = "idx_attendance_date", columnList = "date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // CORE RELATIONS
    // =========================

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_session_id", nullable = false)
    private ClassSession classSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by_teacher_id")
    private Teacher markedByTeacher;

    // =========================
    // ATTENDANCE DATA
    // =========================

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;        // ✅ PRESENT / ABSENT

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceType attendanceType;  // ✅ TEACHER_MANUAL / STUDENT_SELF
}
