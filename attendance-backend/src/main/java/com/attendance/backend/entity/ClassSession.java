package com.attendance.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String status;

    @Column(nullable = false)
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    private double latitude;
    private double longitude;
    private double radius;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Lifecycle flag.
     * TRUE  → class is currently live (now between startTime & endTime)
     * FALSE → future class OR expired class
     *
     * ⚠️ Managed ONLY by scheduler
     */
    @Column(nullable = false)
    private boolean active = false;

    /**
     * Manual cancellation flag.
     * TRUE  → class permanently cancelled by admin/teacher
     * FALSE → valid class
     *
     * ⚠️ Deleted classes must NEVER be auto-activated again
     */
    @Column(nullable = false)
    private boolean deleted = false;
}
