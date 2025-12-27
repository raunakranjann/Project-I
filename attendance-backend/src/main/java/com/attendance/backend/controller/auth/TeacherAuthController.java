package com.attendance.backend.controller.auth;

import com.attendance.backend.dto.TeacherLoginRequest;
import com.attendance.backend.dto.TeacherLoginResponse;
import com.attendance.backend.model.user.Teacher;
import com.attendance.backend.model.user.repository.TeacherRepository;
import com.attendance.backend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherAuthController {

    private static final Logger log =
            LoggerFactory.getLogger(TeacherAuthController.class);

    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public TeacherAuthController(
            TeacherRepository teacherRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // =================================================
    // TEACHER LOGIN (JWT)
    // =================================================
    @PostMapping("/login")
    public TeacherLoginResponse login(
            @RequestBody TeacherLoginRequest request
    ) {

        // ---------- BASIC VALIDATION ----------
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return new TeacherLoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Username and password are required"
            );
        }

        log.info("Teacher login attempt: {}", request.getUsername());

        Teacher teacher = teacherRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (teacher == null) {
            log.warn("Teacher not found: {}", request.getUsername());
            return new TeacherLoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Invalid username"
            );
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                teacher.getPassword())) {

            log.warn("Invalid password for teacher: {}", request.getUsername());
            return new TeacherLoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Invalid password"
            );
        }

        if (!teacher.isActive()) {
            log.warn("Inactive teacher account: {}", request.getUsername());
            return new TeacherLoginResponse(
                    false,
                    null,
                    null,
                    null,
                    "Account disabled"
            );
        }

        // ---------- ISSUE JWT ----------
        String token = jwtUtil.generateToken(
                teacher.getId(),
                "TEACHER"
        );

        log.info("Teacher login successful: id={}", teacher.getId());

        return new TeacherLoginResponse(
                true,
                teacher.getId(),
                teacher.getName(),
                token,
                "Login successful"
        );
    }
}
