package com.attendance.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendance_logs",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "class_id"}
                )
        },
        indexes = {
                @Index(name = "idx_attendance_user", columnList = "user_id"),
                @Index(name = "idx_attendance_class", columnList = "class_id"),
                @Index(name = "idx_attendance_date", columnList = "date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================
    // STUDENT IDENTIFIER
    // ============================
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ============================
    // CLASS IDENTIFIER
    // ============================
    @Column(name = "class_id", nullable = false)
    private Long classId;

    // ============================
    // ATTENDANCE TIME
    // ============================
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // ============================
    // ATTENDANCE DATE (FOR REPORTS)
    // ============================
    @Column(nullable = false)
    private LocalDate date;

    // ============================
    // STATUS (PRESENT / ABSENT)
    // ============================
    @Column(nullable = false, length = 20)
    private String status;
}
