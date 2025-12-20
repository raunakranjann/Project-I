package com.attendance.backend.controller;

import com.attendance.backend.dto.StudentLoginRequest;
import com.attendance.backend.dto.StudentLoginResponse;
import com.attendance.backend.entity.User;
import com.attendance.backend.repository.UserRepository;
import com.attendance.backend.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/student")
@CrossOrigin(origins = "*")
public class StudentLoginController {

    private static final Logger log =
            LoggerFactory.getLogger(StudentLoginController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public StudentLoginController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // =====================================
    // STUDENT LOGIN + DEVICE BINDING + JWT
    // =====================================
    @PostMapping("/login")
    public StudentLoginResponse login(
            @RequestBody StudentLoginRequest request
    ) {

        // ---------- BASIC VALIDATION ----------
        if (request.getRollNo() == null || request.getRollNo().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getDeviceId() == null || request.getDeviceId().isBlank()) {

            return StudentLoginResponse.failed("Missing required fields");
        }

        log.info("Student login attempt: rollNo={}", request.getRollNo());

        User student = userRepository
                .findByRollNo(request.getRollNo())
                .orElse(null);

        if (student == null) {
            log.warn("Invalid roll number: {}", request.getRollNo());
            return StudentLoginResponse.failed("Invalid roll number");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                student.getPassword()
        )) {
            log.warn("Invalid password for rollNo={}", request.getRollNo());
            return StudentLoginResponse.failed("Invalid password");
        }

        // ---------- DEVICE BINDING ----------
        if (student.getDeviceId() == null) {
            // First login → bind device
            student.setDeviceId(request.getDeviceId());
            userRepository.save(student);

            log.info("Device bound for rollNo={}", student.getRollNo());

        } else if (!student.getDeviceId().equals(request.getDeviceId())) {
            // Login from another device → BLOCK
            log.warn("Device mismatch for rollNo={}", student.getRollNo());

            return StudentLoginResponse.failed(
                    "This account is already registered on another device"
            );
        }

        // ---------- ISSUE JWT ----------
        String token = jwtUtil.generateToken(
                student.getId(),
                "STUDENT"
        );

        log.info("Student login successful: id={}, rollNo={}",
                student.getId(), student.getRollNo());

        return new StudentLoginResponse(
                true,
                student.getId(),
                student.getName(),
                token,
                "Login successful"
        );
    }
}
