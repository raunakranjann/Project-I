package com.attendance.backend.controller;

import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final UserRepository userRepo;
    private final ClassSessionRepository classRepo;
    private final AttendanceLogRepository attendanceRepo;

    public AdminDashboardController(
            UserRepository userRepo,
            ClassSessionRepository classRepo,
            AttendanceLogRepository attendanceRepo
    ) {
        this.userRepo = userRepo;
        this.classRepo = classRepo;
        this.attendanceRepo = attendanceRepo;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalClasses", classRepo.count());
        model.addAttribute(
                "todayAttendance",
                attendanceRepo.countByDate(LocalDate.now())
        );

        // Optional: show recent / all classes
        model.addAttribute("classes", classRepo.findAll());

        // MUST MATCH: templates/admin/dashboard.html
        return "admin/dashboard";
    }
}
