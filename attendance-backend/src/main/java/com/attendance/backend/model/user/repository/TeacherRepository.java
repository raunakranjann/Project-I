package com.attendance.backend.model.user.repository;

import com.attendance.backend.model.user.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByUsername(String username);

    boolean existsByUsername(String username);
}
