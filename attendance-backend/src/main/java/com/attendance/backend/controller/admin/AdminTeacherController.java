package com.attendance.backend.controller.admin;

import com.attendance.backend.model.user.Teacher;
import com.attendance.backend.model.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class AdminTeacherController {

    private final TeacherRepository teacherRepo;
    private final PasswordEncoder passwordEncoder;

    // =====================================================
    // ROOT → REDIRECT TO CREATE PAGE
    // =====================================================
    @GetMapping
    public String root() {
        return "redirect:/admin/teachers/new";
    }

    // =====================================================
    // SHOW CREATE TEACHER PAGE
    // =====================================================
    @GetMapping("/new")
    public String showCreateTeacherPage() {
        return "admin/create-teacher";
    }

    // =====================================================
    // HANDLE TEACHER CREATION
    // =====================================================
    @PostMapping("/create")
    public String createTeacher(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {

        // -------------------------------
        // BASIC VALIDATION
        // -------------------------------
        if (username == null || username.isBlank()
                || password == null || password.isBlank()
                || name == null || name.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "All fields are required"
            );
            return "redirect:/admin/teachers/new";
        }

        String normalizedUsername = username.trim().toLowerCase();
        String normalizedName = name.trim();

        // -------------------------------
        // DUPLICATE CHECK
        // -------------------------------
        if (teacherRepo.findByUsername(normalizedUsername).isPresent()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Username already exists"
            );
            return "redirect:/admin/teachers/new";
        }

        // -------------------------------
        // SAVE TEACHER
        // -------------------------------
        Teacher teacher = new Teacher();
        teacher.setUsername(normalizedUsername);
        teacher.setName(normalizedName);
        teacher.setPassword(passwordEncoder.encode(password));
        teacher.setActive(true);

        teacherRepo.save(teacher);

        redirectAttributes.addFlashAttribute(
                "success",
                "Teacher created successfully"
        );

        return "redirect:/admin/teachers/new";
    }
}
