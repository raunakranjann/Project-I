package com.attendance.backend.controller;

import com.attendance.backend.dto.ClassSessionDto;
import com.attendance.backend.repository.ClassSessionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/classes")
@CrossOrigin(origins = "*")
public class ClassController {

    private final ClassSessionRepository classRepo;

    public ClassController(ClassSessionRepository classRepo) {
        this.classRepo = classRepo;
    }

    // =========================================
    // STUDENT → FETCH ACTIVE CLASSES (JWT)
    // =========================================
    @GetMapping("/active")
    public List<ClassSessionDto> getActiveClasses() {

        // Returns empty list if no active classes
        return classRepo.findActiveClassesForStudents(
                LocalDateTime.now()
        );
    }
}
