package com.attendance.backend.controller;

import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ClassSessionRepository classRepo;

    @Autowired
    private AttendanceLogRepository attendanceRepo;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalClasses", classRepo.count());
        model.addAttribute(
                "todayAttendance",
                attendanceRepo.countByDate(LocalDate.now())
        );

        // Optional: recent or all classes (safe)
        model.addAttribute("classes", classRepo.findAll());

        // ✅ MUST MATCH: templates/admin/dashboard.html
        return "admin/dashboard";
    }

    // ================= ATTENDANCE REPORT =================
    @GetMapping("/attendance")
    public String viewAttendance(Model model) {

        model.addAttribute(
                "records",
                attendanceRepo.fetchAttendanceReport()
        );

        // ✅ MUST MATCH: templates/admin/attendance.html
        return "admin/attendance";
    }
}
