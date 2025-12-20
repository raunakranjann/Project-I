package com.attendance.backend.controller;

import com.attendance.backend.entity.Teacher;
import com.attendance.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/teachers")
public class AdminTeacherController {

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===============================
    // SHOW CREATE TEACHER PAGE
    // ===============================
    @GetMapping("/new")
    public String showCreateTeacherPage(Model model) {
        return "admin/create-teacher";
    }

    // ===============================
    // HANDLE TEACHER CREATION
    // ===============================
    @PostMapping
    public String createTeacher(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String password,
            Model model
    ) {

        if (teacherRepo.findByUsername(username).isPresent()) {
            model.addAttribute("message", "Username already exists");
            return "admin/create-teacher";
        }

        Teacher teacher = new Teacher();
        teacher.setUsername(username);
        teacher.setName(name);
        teacher.setPassword(passwordEncoder.encode(password));
        teacher.setActive(true);

        teacherRepo.save(teacher);

        model.addAttribute("message", "Teacher created successfully");
        return "admin/create-teacher";
    }
}
