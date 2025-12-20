package com.attendance.backend.controller;

import com.attendance.backend.dto.TeacherLoginRequest;
import com.attendance.backend.dto.TeacherLoginResponse;
import com.attendance.backend.entity.Teacher;
import com.attendance.backend.repository.TeacherRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherAuthController {

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherAuthController(TeacherRepository teacherRepository,
                                 PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public TeacherLoginResponse login(@RequestBody TeacherLoginRequest request) {

        Teacher teacher = teacherRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (teacher == null) {
            return new TeacherLoginResponse(false, null, null, "Invalid username");
        }

        if (!passwordEncoder.matches(request.getPassword(), teacher.getPassword())) {
            return new TeacherLoginResponse(false, null, null, "Invalid password");
        }

        if (!teacher.isActive()) {
            return new TeacherLoginResponse(false, null, null, "Account disabled");
        }

        return new TeacherLoginResponse(
                true,
                teacher.getId(),
                teacher.getName(),
                "Login successful"
        );
    }
}
