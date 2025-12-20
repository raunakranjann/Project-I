package com.attendance.backend.controller;

import com.attendance.backend.dto.ApiResponse;
import com.attendance.backend.entity.Teacher;
import com.attendance.backend.entity.User;
import com.attendance.backend.repository.TeacherRepository;
import com.attendance.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    // =====================================================
    // STUDENT REGISTRATION (EXISTING - UNCHANGED)
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
            // 1. Upload directory
            String uploadDirPath = "D:/smart-attendance/uploads/users";
            File uploadDir = new File(uploadDirPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 2. Save face image
            String imagePath = uploadDirPath + "/" + rollNo + ".jpg";
            File imageFile = new File(imagePath);
            photo.transferTo(imageFile);

            // 3. Save student
            User user = new User();
            user.setName(name);
            user.setRollNo(rollNo);
            user.setPassword(password);
            user.setPhotoUrl(imagePath);

            userRepository.save(user);

            model.addAttribute("message", "User registered successfully");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Registration failed: " + e.getMessage());
        }

        return "admin/register";
    }


}
