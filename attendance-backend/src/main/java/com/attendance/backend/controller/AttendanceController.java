package com.attendance.backend.controller;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.dto.AttendanceViewDTO;
import com.attendance.backend.entity.AttendanceLog;
import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.User;
import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.service.FaceVerificationService;
import com.attendance.backend.util.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
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

    // =====================================================
    // MARK ATTENDANCE (ANDROID / JWT API)
    // =====================================================
    @PostMapping("/attendance/mark")
    @ResponseBody
    public ApiResponse markAttendance(
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

        if (selfie == null || selfie.isEmpty()) {
            return ApiResponse.failed("Selfie image is required");
        }

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ClassSession session = classRepo.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        // 🔒 BLOCK DELETED CLASSES
        if (session.isDeleted()) {
            return ApiResponse.failed("Class session has been cancelled");
        }

        if (!session.isActive()) {
            return ApiResponse.failed("Class session is no longer active");
        }

        if (attendanceRepo.existsByUserIdAndClassId(studentId, classId)) {
            return ApiResponse.failed("Attendance already marked");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getStartTime())
                || now.isAfter(session.getEndTime())) {
            return ApiResponse.failed("Class is not active at this time");
        }

        double distance = calculateDistance(
                latitude,
                longitude,
                session.getLatitude(),
                session.getLongitude()
        );

        if (distance > session.getRadius()) {
            return ApiResponse.failed("Outside classroom radius");
        }

        File registeredImage =
                new File(USER_IMAGE_DIR + user.getRollNo() + ".jpg");

        if (!registeredImage.exists()) {
            return ApiResponse.failed("Registered face image not found");
        }

        // ================= FACE VERIFICATION =================
        Map<String, Object> faceResult =
                faceService.verifyFace(
                        Files.readAllBytes(registeredImage.toPath()),
                        selfie.getBytes()
                );

        boolean match = Boolean.TRUE.equals(faceResult.get("match"));

        if (!match) {
            return ApiResponse.failed(
                    String.valueOf(
                            faceResult.getOrDefault(
                                    "message",
                                    "Face verification failed"
                            )
                    )
            );
        }

        // ================= SAVE ATTENDANCE =================
        AttendanceLog log = new AttendanceLog();
        log.setUserId(studentId);
        log.setClassId(classId);
        log.setDate(LocalDate.now());
        log.setTimestamp(LocalDateTime.now());
        log.setStatus("PRESENT");

        attendanceRepo.save(log);

        return ApiResponse.success("Attendance marked successfully");
    }

    // =====================================================
    // ADMIN – VIEW ATTENDANCE REPORT
    // =====================================================
    @GetMapping("/admin/attendance")
    public String viewAttendanceReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String status,
            Model model
    ) {

        subject = (subject == null || subject.isBlank()) ? null : subject;
        teacher = (teacher == null || teacher.isBlank()) ? null : teacher;
        status  = (status  == null || status.isBlank())  ? null : status;

        List<AttendanceViewDTO> records =
                attendanceRepo.fetchAttendanceView(
                        date, subject, teacher, status
                );

        model.addAttribute("records", records);
        model.addAttribute("date", date);
        model.addAttribute("subject", subject);
        model.addAttribute("teacher", teacher);
        model.addAttribute("status", status);

        return "admin/attendance";
    }

    // =====================================================
    // ADMIN – EXPORT ATTENDANCE TO EXCEL
    // =====================================================
    @GetMapping("/admin/attendance/export")
    public void exportAttendanceToExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String status,
            HttpServletResponse response
    ) throws Exception {

        subject = (subject == null || subject.isBlank()) ? null : subject;
        teacher = (teacher == null || teacher.isBlank()) ? null : teacher;
        status  = (status  == null || status.isBlank())  ? null : status;

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=attendance-report.xlsx");

        List<AttendanceViewDTO> records =
                attendanceRepo.fetchAttendanceView(
                        date, subject, teacher, status
                );

        OutputStream os = response.getOutputStream();
        ExcelUtil.writeAttendanceExcel(records, os);
        os.flush();
    }

    // =====================================================
    // DISTANCE CALCULATION (HAVERSINE)
    // =====================================================
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
