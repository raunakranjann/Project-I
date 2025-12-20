package com.attendance.backend.controller;

import com.attendance.backend.entity.User;
import com.attendance.backend.repository.TeacherRepository;
import com.attendance.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // 🔐 ADD PASSWORD ENCODER
    @Autowired
    private PasswordEncoder passwordEncoder;

    // =====================================================
    // STUDENT REGISTRATION (SECURE – FIXED)
    // =====================================================

    @GetMapping("/register")
    public String showRegisterPage() {
        return "admin/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String rollNo,
            @RequestParam String password,
            @RequestParam("photo") MultipartFile photo,
            Model model
    ) {
        try {
            // ---------- VALIDATION ----------
            if (userRepository.findByRollNo(rollNo).isPresent()) {
                model.addAttribute("message", "Roll number already exists");
                return "admin/register";
            }

            // ---------- UPLOAD DIRECTORY ----------
            String uploadDirPath = "D:/smart-attendance/uploads/users";
            File uploadDir = new File(uploadDirPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // ---------- SAVE FACE IMAGE ----------
            String imagePath = uploadDirPath + "/" + rollNo + ".jpg";
            File imageFile = new File(imagePath);
            photo.transferTo(imageFile);

            // ---------- SAVE STUDENT (BCrypt PASSWORD) ----------
            User user = new User();
            user.setName(name);
            user.setRollNo(rollNo);

            // 🔐 IMPORTANT FIX (THIS SOLVES YOUR ISSUE)
            user.setPassword(passwordEncoder.encode(password));

            user.setPhotoUrl(imagePath);

            // deviceId stays NULL → first login will bind it
            user.setDeviceId(null);

            userRepository.save(user);

            model.addAttribute("message", "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute(
                    "message",
                    "Registration failed: " + e.getMessage()
            );
        }

        return "admin/register";
    }
}
