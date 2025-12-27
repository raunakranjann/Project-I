package com.attendance.backend.model.user;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String rollNo;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String deviceId;

    private String photoUrl;

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
    private Semester semester;


    @ManyToOne
    @JoinColumn(name = "current_semester_id")
    private Semester currentSemester;



}
