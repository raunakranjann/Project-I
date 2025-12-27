package com.attendance.backend.model.user.repository;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNo(String rollNo);

    List<Student> findByCourseAndBranchAndAcademicSession(
            Course course,
            Branch branch,
            AcademicSession academicSession
    );
}
