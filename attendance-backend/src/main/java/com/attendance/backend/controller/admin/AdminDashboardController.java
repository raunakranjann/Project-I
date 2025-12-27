package com.attendance.backend.controller.admin;

import com.attendance.backend.model.attendance.repository.AttendanceRecordRepository;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import com.attendance.backend.model.user.repository.StudentRepository;
import com.attendance.backend.model.user.repository.TeacherRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATION')")
public class AdminDashboardController {

    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final ClassSessionRepository classRepo;
    private final AttendanceRecordRepository attendanceRepo;

    // =====================================================
    // ADMIN / ADMINISTRATION DASHBOARD
    // =====================================================
    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            HttpServletResponse response,
            Model model
    ) {

        // ================= ROLE CHECK =================
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);

        boolean isAdministration = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMINISTRATION"::equals);

        // ADMINISTRATION → redirect to their dashboard
        if (isAdministration && !isAdmin) {
            return "redirect:/administration/dashboard";
        }

        // ================= CACHE CONTROL =================
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // ================= SUMMARY COUNTS =================
        model.addAttribute("totalStudents", studentRepo.count());
        model.addAttribute("totalTeachers", teacherRepo.count());
        model.addAttribute("activeClassesCount", classRepo.countActiveClasses());

        // ================= CLASS LISTS =================
        model.addAttribute(
                "activeClasses",
                classRepo.findByActiveTrueAndDeletedFalse()
        );

        model.addAttribute(
                "deletedClasses",
                classRepo.findByDeletedTrue()
        );

        // ================= ATTENDANCE =================
        model.addAttribute(
                "todayAttendance",
                attendanceRepo.countByDate(LocalDate.now())
        );

        return "admin/dashboard";
    }
}
