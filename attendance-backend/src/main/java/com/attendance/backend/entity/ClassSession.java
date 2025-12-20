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

    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    private double latitude;
    private double longitude;
    private double radius;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean active = true;   // ✅ soft delete
}
