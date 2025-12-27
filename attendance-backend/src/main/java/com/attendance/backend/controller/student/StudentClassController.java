package com.attendance.backend.controller.student;

import com.attendance.backend.dto.ClassSessionDto;
import com.attendance.backend.model.user.Student;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import com.attendance.backend.model.user.repository.StudentRepository;
import com.attendance.backend.service.ClassSessionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/classes")
@CrossOrigin(origins = "*")
public class StudentClassController {

    private final ClassSessionRepository classRepo;
    private final StudentRepository studentRepo;
    private final ClassSessionService classSessionService;

    public StudentClassController(
            ClassSessionRepository classRepo,
            StudentRepository studentRepo,
            ClassSessionService classSessionService
    ) {
        this.classRepo = classRepo;
        this.studentRepo = studentRepo;
        this.classSessionService = classSessionService;
    }

    // =========================================
// STUDENT → TODAY'S CLASSES (READ-ONLY)
// =========================================
    @GetMapping("/today")
    public List<ClassSessionDto> getTodayClasses(
            Authentication authentication
    ) {

        // Ensure today's sessions exist
        classSessionService.generateClassesForDate(LocalDate.now());

        Long studentId = (Long) authentication.getPrincipal();

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return classRepo.findStudentClassesForToday(
                student.getCourse(),
                student.getBranch(),
                student.getAcademicSession(),
                LocalDate.now()
        );
    }


    // =========================================
    // STUDENT → THEIR FUTURE + LIVE CLASSES ONLY
    // =========================================
    @GetMapping("/active")
    public List<ClassSessionDto> getMyClasses(
            Authentication authentication
    ) {

        // 🔑 Ensure today's classes exist
        classSessionService.generateClassesForDate(LocalDate.now());

        Long studentId = (Long) authentication.getPrincipal();

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return classRepo.findStudentClassesByRoutine(
                student.getCourse(),
                student.getBranch(),
                student.getAcademicSession(),
                LocalDateTime.now()
        );
    }
}
