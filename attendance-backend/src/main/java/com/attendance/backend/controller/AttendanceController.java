package com.attendance.backend.controller;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.entity.AttendanceLog;
import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.User;
import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.service.FaceVerificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private static final String USER_IMAGE_DIR =
            "D:/smart-attendance/uploads/users/";

    private final FaceVerificationService faceService;
    private final AttendanceLogRepository attendanceRepo;
    private final ClassSessionRepository classRepo;
    private final UserRepository userRepo;

    public AttendanceController(
            FaceVerificationService faceService,
            AttendanceLogRepository attendanceRepo,
            ClassSessionRepository classRepo,
            UserRepository userRepo
    ) {
        this.faceService = faceService;
        this.attendanceRepo = attendanceRepo;
        this.classRepo = classRepo;
        this.userRepo = userRepo;
    }

    // ======================================
    // MARK ATTENDANCE (JWT REQUIRED)
    // ======================================
    @PostMapping("/mark")
    public ApiResponse markAttendance(
            Authentication authentication,
            @RequestParam Long classId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam MultipartFile selfie
    ) throws Exception {

        // ---------- AUTH CHECK ----------
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.failed("Unauthorized");
        }

        Long studentId = (Long) authentication.getPrincipal();

        // ---------- BASIC VALIDATION ----------
        if (selfie == null || selfie.isEmpty()) {
            return ApiResponse.failed("Selfie image is required");
        }

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassSession session = classRepo.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (!session.isActive()) {
            return ApiResponse.failed("Class is no longer active");
        }


        // ---------- DUPLICATE CHECK ----------
        if (attendanceRepo.existsByUserIdAndClassId(studentId, classId)) {
            return ApiResponse.failed("Attendance already marked");
        }

        // ---------- TIME WINDOW ----------
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) ||
                now.isAfter(session.getEndTime())) {
            return ApiResponse.failed("Class is not active");
        }

        // ---------- GPS CHECK ----------
        double distance = calculateDistance(
                latitude,
                longitude,
                session.getLatitude(),
                session.getLongitude()
        );

        if (distance > session.getRadius()) {
            return ApiResponse.failed("Outside classroom radius");
        }

        // ---------- FACE VERIFICATION ----------
        File registeredImage = new File(
                USER_IMAGE_DIR + user.getRollNo() + ".jpg"
        );

        if (!registeredImage.exists()) {
            return ApiResponse.failed("Registered face image not found");
        }

        boolean match = faceService.verifyFace(
                Files.readAllBytes(registeredImage.toPath()),
                selfie.getBytes()
        );

        if (!match) {
            return ApiResponse.failed("Face mismatch");
        }

        // ---------- SAVE ATTENDANCE ----------
        AttendanceLog log = new AttendanceLog();
        log.setUserId(studentId);
        log.setClassId(classId);
        log.setTimestamp(LocalDateTime.now());
        log.setDate(LocalDate.now());
        log.setStatus("PRESENT");

        attendanceRepo.save(log);

        return ApiResponse.success("Attendance marked successfully");
    }

    // ======================================
    // DISTANCE (HAVERSINE)
    // ======================================
    private double calculateDistance(
            double lat1, double lon1, double lat2, double lon2
    ) {
        double R = 6371e3;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
