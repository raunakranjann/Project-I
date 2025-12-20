package com.attendance.backend.controller;

import com.attendance.backend.dto.CreateClassRequest;
import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.Teacher;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherClassController {

    private static final Logger log =
            LoggerFactory.getLogger(TeacherClassController.class);

    private final ClassSessionRepository classRepo;
    private final TeacherRepository teacherRepo;

    public TeacherClassController(
            ClassSessionRepository classRepo,
            TeacherRepository teacherRepo
    ) {
        this.classRepo = classRepo;
        this.teacherRepo = teacherRepo;
    }

    // ==================================================
    // TEACHER DASHBOARD → LOAD ACTIVE CLASSES (JWT)
    // ==================================================
    @GetMapping("/classes")
    public ResponseEntity<?> getTeacherClasses(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        Long teacherId = (Long) authentication.getPrincipal();
        log.info("Loading classes for teacherId={}", teacherId);

        List<ClassSession> sessions =
                classRepo.findByTeacher_IdAndActiveTrue(teacherId);

        return ResponseEntity.ok(
                sessions.stream().map(c -> Map.of(
                        "id", c.getId(),
                        "subjectName", c.getSubjectName(),
                        "latitude", c.getLatitude(),
                        "longitude", c.getLongitude(),
                        "radius", c.getRadius(),
                        "startTime", c.getStartTime().toString(),
                        "endTime", c.getEndTime().toString()
                )).toList()
        );
    }

    // ==================================================
    // CREATE CLASS (JWT → BACKEND)
    // ==================================================
    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(
            @RequestBody CreateClassRequest req,
            Authentication authentication
    ) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body(
                    Map.of("status", "FAILED", "message", "Unauthorized")
            );
        }

        Long teacherId = (Long) authentication.getPrincipal();
        log.info("Create class request by teacherId={}", teacherId);

        // ---------- BASIC VALIDATION ----------
        if (req.getSubjectName() == null || req.getSubjectName().isBlank()
                || req.getStartTime() == null
                || req.getEndTime() == null) {

            return ResponseEntity.badRequest().body(
                    Map.of("status", "FAILED",
                            "message", "Missing required fields")
            );
        }

        if (req.getStartTime().isAfter(req.getEndTime())) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "FAILED",
                            "message", "Start time must be before end time")
            );
        }

        if (req.getLatitude() == 0 || req.getLongitude() == 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "FAILED",
                            "message", "Invalid GPS coordinates")
            );
        }

        if (req.getRadius() <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", "FAILED",
                            "message", "Radius must be greater than zero")
            );
        }

        // ---------- FETCH TEACHER ENTITY ----------
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> {
                    log.error("Teacher not found for id={}", teacherId);
                    return new RuntimeException("Teacher not found");
                });

        // ---------- CREATE SESSION ----------
        ClassSession session = new ClassSession();
        session.setTeacher(teacher);        // ✅ REQUIRED
        session.setSubjectName(req.getSubjectName());
        session.setLatitude(req.getLatitude());
        session.setLongitude(req.getLongitude());
        session.setRadius(req.getRadius());
        session.setStartTime(req.getStartTime());
        session.setEndTime(req.getEndTime());
        session.setActive(true);            // ✅ soft delete default

        classRepo.save(session);

        log.info("Class created successfully. classId={}", session.getId());

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "classId", session.getId(),
                        "message", "Class created successfully"
                )
        );
    }
}
