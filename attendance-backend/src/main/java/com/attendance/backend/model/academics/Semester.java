package com.attendance.backend.model.academics;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "semesters",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"course_id", "branch_id", "academic_session_id", "number"}
                )
        }
)
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number; // 1,2,3,4...

    @ManyToOne(optional = false)
    private Course course;

    @ManyToOne(optional = false)
    private Branch branch;

    @ManyToOne(optional = false)
    private AcademicSession academicSession;

    @Column(nullable = false)
    private boolean active = true;
}
