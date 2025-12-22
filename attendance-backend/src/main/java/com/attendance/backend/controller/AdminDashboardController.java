package com.attendance.backend.controller;

import com.attendance.backend.repository.AttendanceLogRepository;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.TeacherRepository;
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
    private final TeacherRepository teacherRepo;
    private final ClassSessionRepository classRepo;
    private final AttendanceLogRepository attendanceRepo;

    public AdminDashboardController(
            UserRepository userRepo,
            TeacherRepository teacherRepo,
            ClassSessionRepository classRepo,
            AttendanceLogRepository attendanceRepo
    ) {
        this.userRepo = userRepo;
        this.teacherRepo = teacherRepo;
        this.classRepo = classRepo;
        this.attendanceRepo = attendanceRepo;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // ================= COUNTS =================
        model.addAttribute("totalStudents", userRepo.count());
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
