package com.attendance.backend.model.routine;

import com.attendance.backend.model.user.Teacher;
import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(
        name = "class_sessions",
        indexes = {
                @Index(
                        name = "idx_class_session_teacher_date",
                        columnList = "teacher_id, session_date"
                ),
                @Index(
                        name = "idx_class_session_date",
                        columnList = "session_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subjectName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;


    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    private double latitude;
    private double longitude;
    private double radius;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean active = false;

    @Column(nullable = false)
    private boolean deleted = false;
}
