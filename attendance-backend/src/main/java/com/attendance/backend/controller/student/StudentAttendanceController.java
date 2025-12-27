package com.attendance.backend.controller.student;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.model.attendance.AttendanceRecord;
import com.attendance.backend.model.attendance.AttendanceStatus;
import com.attendance.backend.model.attendance.AttendanceType;
import com.attendance.backend.model.attendance.repository.AttendanceRecordRepository;
import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import com.attendance.backend.model.user.Student;
import com.attendance.backend.model.user.repository.StudentRepository;
import com.attendance.backend.service.FaceVerificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class StudentAttendanceController {

    private final Path userImageDir;
    private final FaceVerificationService faceService;
    private final AttendanceRecordRepository attendanceRepo;
    private final ClassSessionRepository classRepo;
    private final StudentRepository studentRepo;

    public StudentAttendanceController(
            @Value("${attendance.user-image-dir}") String userImageDir,
            FaceVerificationService faceService,
            AttendanceRecordRepository attendanceRepo,
            ClassSessionRepository classRepo,
            StudentRepository studentRepo
    ) throws Exception {
        this.userImageDir = Path.of(userImageDir);
        Files.createDirectories(this.userImageDir);
        this.faceService = faceService;
        this.attendanceRepo = attendanceRepo;
        this.classRepo = classRepo;
        this.studentRepo = studentRepo;
    }

    // =====================================================
    // STUDENT SELF ATTENDANCE (JWT)
    // =====================================================
    @PostMapping("/mark")
    @Transactional
    public ApiResponse<String> markAttendance(
            Authentication authentication,
            @RequestParam Long classId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam MultipartFile selfie
    ) throws Exception {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.failed("Unauthorized");
        }

        Long studentId = (Long) authentication.getPrincipal();

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("Student not found"));

        ClassSession session = classRepo.findById(classId)
                .orElseThrow(() -> new IllegalStateException("Class not found"));

        // 🔒 Class must be active
        if (session.isDeleted() || !session.isActive()) {
            return ApiResponse.failed("Class not active");
        }

        // 🔒 Attendance only once
        if (attendanceRepo.existsByStudentIdAndClassSessionId(studentId, classId)) {
            return ApiResponse.failed("Attendance already marked");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) || now.isAfter(session.getEndTime())) {
            return ApiResponse.failed("Class not active now");
        }

        // 🔒 Geo-fence check
        double distance = calculateDistance(
                latitude, longitude,
                session.getLatitude(), session.getLongitude()
        );

        if (distance > session.getRadius()) {
            return ApiResponse.failed("Outside classroom radius");
        }

        // 🔒 Face verification
        Path registeredImage =
                userImageDir.resolve(student.getRollNo() + ".jpg");

        if (!Files.exists(registeredImage)) {
            return ApiResponse.failed("Registered face image not found");
        }

        Map<String, Object> faceResult =
                faceService.verifyFace(
                        Files.readAllBytes(registeredImage),
                        selfie.getBytes()
                );

        if (!Boolean.TRUE.equals(faceResult.get("match"))) {
            return ApiResponse.failed(
                    String.valueOf(
                            faceResult.getOrDefault("message", "Face verification failed")
                    )
            );
        }

        // ✅ SAVE ATTENDANCE (ENUM SAFE)
        AttendanceRecord record = new AttendanceRecord();
        record.setStudent(student);
        record.setClassSession(session);
        record.setDate(LocalDate.now());
        record.setTimestamp(now);
        record.setStatus(AttendanceStatus.PRESENT);        // ✅ ENUM
        record.setAttendanceType(AttendanceType.STUDENT_SELF);

        attendanceRepo.save(record);

        return ApiResponse.successMessage("Attendance marked successfully");
    }

    // =====================================================
    // DISTANCE (HAVERSINE)
    // =====================================================
    private double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2
    ) {
        double R = 6371e3;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
