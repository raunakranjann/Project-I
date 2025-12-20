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

    @GetMapping("/active")
    public List<ClassSessionDto> getActiveClasses() {
        return classRepo.findActiveClassesForStudents(
                LocalDateTime.now()
        );
    }
}
