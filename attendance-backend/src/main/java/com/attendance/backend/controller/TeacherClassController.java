package com.attendance.backend.controller;

import com.attendance.backend.dto.CreateClassRequest;
import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.Teacher;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.TeacherRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherClassController {

    private final TeacherRepository teacherRepo;
    private final ClassSessionRepository classRepo;

    public TeacherClassController(
            TeacherRepository teacherRepo,
            ClassSessionRepository classRepo
    ) {
        this.teacherRepo = teacherRepo;
        this.classRepo = classRepo;
    }


    // ============================================
// TEACHER DASHBOARD → LOAD CLASSES
// ============================================
    @GetMapping("/classes")
    public ResponseEntity<?> getTeacherClasses(
            @RequestParam Long teacherId
    ) {

        List<ClassSession> sessions =
                classRepo.findByTeacher_Id(teacherId);

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
    // CREATE CLASS (ANDROID → BACKEND)
    // ==================================================
    @PostMapping("/create-class")
    public ResponseEntity<?> createClass(
            @RequestBody CreateClassRequest req
    ) {

        // ---------- BASIC VALIDATION ----------
        if (req.getTeacherId() == null ||
                req.getSubjectName() == null ||
                req.getSubjectName().isBlank() ||
                req.getStartTime() == null ||
                req.getEndTime() == null) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "FAILED",
                            "message", "Missing required fields"
                    )
            );
        }

        if (req.getLatitude() == 0 || req.getLongitude() == 0) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "FAILED",
                            "message", "Invalid GPS coordinates"
                    )
            );
        }

        if (req.getRadius() <= 0) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "status", "FAILED",
                            "message", "Radius must be greater than zero"
                    )
            );
        }

        // ---------- FETCH TEACHER ----------
        Teacher teacher = teacherRepo.findById(req.getTeacherId())
                .orElseThrow(() ->
                        new RuntimeException("Teacher not found")
                );

        // ---------- CREATE SESSION ----------
        ClassSession session = new ClassSession();
        session.setTeacher(teacher);
        session.setSubjectName(req.getSubjectName());
        session.setLatitude(req.getLatitude());
        session.setLongitude(req.getLongitude());
        session.setRadius(req.getRadius());
        session.setStartTime(req.getStartTime());
        session.setEndTime(req.getEndTime());

        classRepo.save(session);

        // ---------- SUCCESS RESPONSE ----------
        return ResponseEntity.ok(
                Map.of(
                        "status", "SUCCESS",
                        "classId", session.getId(),
                        "message", "Class created successfully"
                )
        );
    }
}
