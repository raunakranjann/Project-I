package com.attendance.backend.controller.admin;

import com.attendance.backend.dto.AttendanceViewDTO;
import com.attendance.backend.model.academics.repository.*;
import com.attendance.backend.model.attendance.repository.AttendanceRecordRepository;
import com.attendance.backend.util.ExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAttendanceController {

    private final AttendanceRecordRepository attendanceRepo;
    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final SemesterRepository semesterRepo;

    // =====================================================
    // PAGE VIEW
    // =====================================================
    @GetMapping
    public String view(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String student,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long academicSessionId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        Page<AttendanceViewDTO> records =
                attendanceRepo.fetchAttendanceViewPaged(
                        date,
                        empty(subject),
                        empty(teacher),
                        empty(student),
                        empty(status),
                        courseId,
                        branchId,
                        academicSessionId,
                        semesterId,
                        PageRequest.of(page, 20, Sort.by("timestamp").descending())
                );

        model.addAttribute("records", records);
        model.addAttribute("courses", courseRepo.findAll());
        model.addAttribute("sessions", sessionRepo.findAll());

        if (courseId != null)
            model.addAttribute("branches", branchRepo.findByCourseId(courseId));

        if (courseId != null && branchId != null && academicSessionId != null)
            model.addAttribute("semesters",
                    semesterRepo.findByCourseIdAndBranchIdAndAcademicSessionId(
                            courseId, branchId, academicSessionId));

        model.addAttribute("date", date);
        model.addAttribute("subject", subject);
        model.addAttribute("teacher", teacher);
        model.addAttribute("student", student);
        model.addAttribute("status", status);
        model.addAttribute("courseId", courseId);
        model.addAttribute("branchId", branchId);
        model.addAttribute("academicSessionId", academicSessionId);
        model.addAttribute("semesterId", semesterId);

        return "admin/attendance";
    }

    // =====================================================
    // EXCEL EXPORT
    // =====================================================
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String student,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long academicSessionId,
            @RequestParam(required = false) Long semesterId,
            HttpServletResponse response
    ) throws Exception {

        List<AttendanceViewDTO> data =
                attendanceRepo.fetchAttendanceView(
                        date,
                        empty(subject),
                        empty(teacher),
                        empty(student),
                        empty(status),
                        courseId,
                        branchId,
                        academicSessionId,
                        semesterId
                );

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=attendance.xlsx");

        ExcelUtil.writeAttendanceExcel(data, response.getOutputStream());
    }

    private String empty(String v) {
        return (v == null || v.isBlank()) ? null : v;
    }
}
