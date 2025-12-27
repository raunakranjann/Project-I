package com.attendance.backend.model.routine;

import com.attendance.backend.model.user.Teacher;
import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Entity
@Getter
@Setter
@Table(
        name = "weekly_routines",
        uniqueConstraints = {

                // =================================================
                // ONE CLASS SLOT PER SEMESTER
                // =================================================
                @UniqueConstraint(
                        name = "uk_class_slot_semester",
                        columnNames = {
                                "course_id",
                                "branch_id",
                                "academic_session_id",
                                "semester_id",
                                "day_of_week",
                                "period_no"
                        }
                ),

                // =================================================
                // ONE TEACHER SLOT PER SEMESTER
                // =================================================
                @UniqueConstraint(
                        name = "uk_teacher_slot_semester",
                        columnNames = {
                                "teacher_id",
                                "semester_id",
                                "day_of_week",
                                "period_no"
                        }
                )
        }
)
public class WeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // ACADEMIC CONTEXT
    // ===============================

    @ManyToOne(optional = false)
    @JoinColumn(name = "timeslot_id", nullable = false)
    private Timeslot timeslot;



    @ManyToOne(optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(optional = false)
    @JoinColumn(name = "academic_session_id", nullable = false)
    private AcademicSession academicSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    // ===============================
    // ROUTINE SLOT
    // ===============================

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "period_no", nullable = false)
    private int periodNo;

    @Column(nullable = false)
    private String subjectName;

    @ManyToOne(optional = true)
    @JoinColumn(name = "teacher_id", nullable = true)
    private Teacher teacher;



    // ===============================
    // STATUS
    // ===============================

    @Column(nullable = false)
    private boolean active = true;
}
