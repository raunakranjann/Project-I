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

import java.time.LocalDateTime;
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
    // TEACHER DASHBOARD → FUTURE + LIVE CLASSES
    // ==================================================
    @GetMapping("/classes")
    public ResponseEntity<?> getTeacherClasses(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        Long teacherId = (Long) authentication.getPrincipal();

        List<ClassSession> sessions =
                classRepo.findTeacherCurrentAndFutureClasses(
                        teacherId,
                        LocalDateTime.now()
                );

        return ResponseEntity.ok(
                sessions.stream().map(c -> Map.of(
                        "id", c.getId(),
                        "subjectName", c.getSubjectName(),
                        "latitude", c.getLatitude(),
                        "longitude", c.getLongitude(),
                        "radius", c.getRadius(),
                        "startTime", c.getStartTime().toString(),
                        "endTime", c.getEndTime().toString(),
                        "active", c.isActive()
                )).toList()
        );
    }

    // ==================================================
    // CREATE CLASS
    // ==================================================
    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(
            @RequestBody CreateClassRequest req,
            Authentication authentication
    ) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("status", "FAILED", "message", "Unauthorized"));
        }

        Long teacherId = (Long) authentication.getPrincipal();

        // ---------- VALIDATION ----------
        if (req.getSubjectName() == null || req.getSubjectName().isBlank()
                || req.getStartTime() == null
                || req.getEndTime() == null) {

            return ResponseEntity.badRequest()
                    .body(Map.of("status", "FAILED", "message", "Missing required fields"));
        }

        if (req.getStartTime().isAfter(req.getEndTime())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "FAILED", "message", "Start time must be before end time"));
        }

        if (req.getRadius() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "FAILED", "message", "Radius must be greater than zero"));
        }

        // ---------- OVERLAP CHECK ----------
        boolean hasConflict =
                classRepo.existsOverlappingClassForTeacher(
                        teacherId,
                        req.getStartTime(),
                        req.getEndTime()
                );

        if (hasConflict) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "FAILED",
                            "message", "You already have a class scheduled in this time frame"
                    )
            );
        }

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // ---------- CREATE SESSION ----------
        ClassSession session = new ClassSession();
        session.setTeacher(teacher);
        session.setSubjectName(req.getSubjectName());
        session.setLatitude(req.getLatitude());
        session.setLongitude(req.getLongitude());
        session.setRadius(req.getRadius());
        session.setStartTime(req.getStartTime());
        session.setEndTime(req.getEndTime());

        // lifecycle flags
        session.setActive(false);     // scheduler will activate
        session.setDeleted(false);    // important

        classRepo.save(session);

        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "classId", session.getId(),
                        "message", "Class created successfully"
                )
        );
    }

    // ==================================================
    // DELETE CLASS (MANUAL DELETE)
    // ==================================================
    @DeleteMapping("/classes/{classId}")
    public ResponseEntity<?> deleteClass(
            @PathVariable Long classId,
            Authentication authentication
    ) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        Long teacherId = (Long) authentication.getPrincipal();

        ClassSession session = classRepo.findById(classId).orElse(null);

        if (session == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Class not found"));
        }

        if (!session.getTeacher().getId().equals(teacherId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Access denied"));
        }

        // ✅ CORRECT MANUAL DELETE
        session.setDeleted(true);
        session.setActive(false);

        classRepo.save(session);

        return ResponseEntity.ok(
                Map.of("message", "Class deleted successfully")
        );
    }
}
