package com.attendance.backend.controller.teacher;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.dto.StudentAttendanceDto;
import com.attendance.backend.dto.TeacherClassSessionDto;
import com.attendance.backend.model.attendance.AttendanceRecord;
import com.attendance.backend.model.attendance.AttendanceStatus;
import com.attendance.backend.model.attendance.AttendanceType;
import com.attendance.backend.model.attendance.repository.AttendanceRecordRepository;
import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import com.attendance.backend.model.user.Student;
import com.attendance.backend.model.user.repository.StudentRepository;
import com.attendance.backend.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')")
public class TeacherDashboardController {

    private final ClassSessionRepository classRepo;
    private final StudentRepository studentRepo;
    private final AttendanceRecordRepository attendanceRepo;
    private final ClassSessionService classSessionService;

    // =====================================================
    // UTILITY → EXTRACT TEACHER ID (JWT PRINCIPAL = Long)
    // =====================================================
    private Long getTeacherId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long id) return id;
        throw new IllegalStateException("Invalid authentication principal");
    }

    // =====================================================
    // TEACHER → TODAY'S CLASSES
    // =====================================================
    @GetMapping("/classes/today")
    public List<TeacherClassSessionDto> getTodayClasses(
            Authentication authentication
    ) {
        classSessionService.generateClassesForDate(LocalDate.now());

        Long teacherId = getTeacherId(authentication);

        LocalDate today = LocalDate.now();
        return classRepo.findTeacherClassesForDate(
                teacherId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    // =====================================================
    // TEACHER → STUDENTS FOR LIVE CLASS
    // =====================================================
    @GetMapping("/classes/{classId}/students")
    public ApiResponse<List<StudentAttendanceDto>> getStudentsForLiveClass(
            Authentication authentication,
            @PathVariable Long classId
    ) {
        Long teacherId = getTeacherId(authentication);

        ClassSession session = classRepo.findById(classId).orElse(null);
        if (session == null)
            return ApiResponse.failed("Class not found");

        if (!session.getTeacher().getId().equals(teacherId))
            return ApiResponse.failed("Unauthorized");

        LocalDateTime now = LocalDateTime.now();
        if (!session.isActive()
                || session.isDeleted()
                || now.isBefore(session.getStartTime())
                || now.isAfter(session.getEndTime())) {
            return ApiResponse.failed("Class is not live");
        }

        List<Student> students =
                studentRepo.findByCourseAndBranchAndAcademicSession(
                        session.getCourse(),
                        session.getBranch(),
                        session.getAcademicSession()
                );

        List<StudentAttendanceDto> result =
                students.stream()
                        .map(s -> new StudentAttendanceDto(
                                s.getId(),
                                s.getRollNo(),
                                s.getName(),
                                attendanceRepo.findStatusForStudentInClass(
                                        classId, s.getId()
                                )
                        ))
                        .toList();

        return ApiResponse.success(result);
    }

    // =====================================================
    // TEACHER → MARK ATTENDANCE (MANUAL)
    // =====================================================
    @PostMapping("/attendance/mark")
    @Transactional
    public ApiResponse<String> markAttendanceByTeacher(
            Authentication authentication,
            @RequestParam Long classId,
            @RequestParam Long studentId,
            @RequestParam String status   // PRESENT / ABSENT (from Android)
    ) {
        Long teacherId = getTeacherId(authentication);

        // 🔒 Convert + validate status
        AttendanceStatus attendanceStatus;
        try {
            attendanceStatus = AttendanceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ApiResponse.failed("Invalid attendance status");
        }

        if (attendanceRepo.existsByStudentIdAndClassSessionId(studentId, classId))
            return ApiResponse.failed("Attendance already marked");

        ClassSession session = classRepo.findById(classId).orElse(null);
        if (session == null)
            return ApiResponse.failed("Class not found");

        if (!session.getTeacher().getId().equals(teacherId))
            return ApiResponse.failed("Unauthorized");

        LocalDateTime now = LocalDateTime.now();
        if (!session.isActive()
                || session.isDeleted()
                || now.isBefore(session.getStartTime())
                || now.isAfter(session.getEndTime())) {
            return ApiResponse.failed("Class is not live");
        }

        Student student = studentRepo.findById(studentId).orElse(null);
        if (student == null)
            return ApiResponse.failed("Student not found");

        AttendanceRecord record = new AttendanceRecord();
        record.setStudent(student);
        record.setClassSession(session);
        record.setDate(LocalDate.now());
        record.setTimestamp(now);
        record.setStatus(attendanceStatus);                 // ✅ CORRECT ENUM
        record.setAttendanceType(AttendanceType.TEACHER_MANUAL);
        record.setMarkedByTeacher(session.getTeacher());

        attendanceRepo.save(record);

        return ApiResponse.successMessage("Attendance marked successfully");
    }
}
