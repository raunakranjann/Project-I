package com.attendance.backend.controller.administration;

import com.attendance.backend.model.academics.repository.AcademicSessionRepository;
import com.attendance.backend.model.academics.repository.BranchRepository;
import com.attendance.backend.model.academics.repository.CourseRepository;
import com.attendance.backend.model.academics.repository.SemesterRepository;
import com.attendance.backend.model.user.repository.TeacherRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/administration")
@RequiredArgsConstructor
public class AdministrationDashboardController {

    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final SemesterRepository semesterRepo;   // ✅ NEW
    private final TeacherRepository teacherRepo;

    // =====================================================
    // ADMINISTRATION DASHBOARD
    // =====================================================
    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            HttpServletResponse response,
            Model model
    ) {

        // ================= STRICT ROLE CHECK =================
        boolean isAdministration = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATION"));

        if (!isAdministration) {
            throw new AccessDeniedException("Administration access only");
        }

        // ================= CACHE CONTROL =================
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // ================= DASHBOARD METRICS =================
        model.addAttribute("courseCount", courseRepo.count());
        model.addAttribute("branchCount", branchRepo.count());
        model.addAttribute("sessionCount", sessionRepo.count());
        model.addAttribute("semesterCount", semesterRepo.count()); // ✅ NEW
        model.addAttribute("teacherCount", teacherRepo.count());

        return "administration/dashboard";
    }
}
