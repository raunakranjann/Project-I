package com.attendance.backend.model.academics.repository;

import com.attendance.backend.model.academics.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByName(String name);
}
