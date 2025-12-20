package com.attendance.backend.controller;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.entity.AttendanceLog;
import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.User;
import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.service.FaceVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FaceVerificationService faceService;

    @Autowired
    private AttendanceLogRepository attendanceRepo;

    @Autowired
    private ClassSessionRepository classRepo;

    @Autowired
    private UserRepository userRepo;

    @PostMapping("/mark")
    public ApiResponse markAttendance(
            @RequestParam Long userId,
            @RequestParam Long classId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam MultipartFile selfie
    ) throws Exception {

        // 1️⃣ Load user
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Load class
        ClassSession session = classRepo.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // 3️⃣ Prevent duplicate attendance (✅ CLASS-SCOPED)
        if (attendanceRepo.existsByUserIdAndClassId(userId, classId)) {
            return new ApiResponse(
                    "FAIL",
                    "Attendance already marked for this class"
            );
        }

        // 4️⃣ Time window check
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime()) ||
                now.isAfter(session.getEndTime())) {
            return new ApiResponse(
                    "FAIL",
                    "Class is not active at this time"
            );
        }

        // 5️⃣ GPS radius check
        double distance = calculateDistance(
                latitude,
                longitude,
                session.getLatitude(),
                session.getLongitude()
        );

        if (distance > session.getRadius()) {
            return new ApiResponse("FAIL", "Outside classroom radius");
        }

        // 6️⃣ Load registered face
        File registeredImageFile =
                new File("D:/smart-attendance/uploads/users/" +
                        user.getRollNo() + ".jpg");

        if (!registeredImageFile.exists()) {
            return new ApiResponse("FAIL", "Registered face image not found");
        }

        byte[] registeredImageBytes =
                Files.readAllBytes(registeredImageFile.toPath());

        // 7️⃣ Face verification
        boolean faceMatch = faceService.verifyFace(
                registeredImageBytes,
                selfie.getBytes()
        );

        if (!faceMatch) {
            return new ApiResponse("FAIL", "Face mismatch");
        }

        // 8️⃣ Save attendance
        AttendanceLog log = new AttendanceLog();
        log.setUserId(userId);
        log.setClassId(classId);
        log.setTimestamp(LocalDateTime.now());
        log.setDate(LocalDate.now());
        log.setStatus("PRESENT");

        attendanceRepo.save(log);

        return new ApiResponse(
                "SUCCESS",
                "Attendance marked successfully"
        );
    }

    // ===============================
    // Distance calculation (Haversine)
    // ===============================
    private double calculateDistance(
            double lat1,
            double lon1,
            double lat2,
            double lon2
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
