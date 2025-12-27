package com.attendance.backend.controller.admin;

import com.attendance.backend.model.user.Admin;
import com.attendance.backend.model.user.AdminRole;
import com.attendance.backend.model.user.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserManagementController {

    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;

    // ================================
    // LIST ADMIN USERS
    // ================================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("admins", adminRepo.findAll());
        return "admin/admin-users";
    }

    // ================================
    // CREATE ADMINISTRATION USER
    // ================================
    @PostMapping("/administration")
    public String createAdministration(
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {

        // -------------------------------
        // BASIC VALIDATION
        // -------------------------------
        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Username and password are required"
            );
            return "redirect:/admin/users";
        }

        String normalizedUsername = username.trim().toLowerCase();

        // -------------------------------
        // DUPLICATE CHECK
        // -------------------------------
        if (adminRepo.findByUsername(normalizedUsername).isPresent()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Username already exists"
            );
            return "redirect:/admin/users";
        }

        // -------------------------------
        // SAVE ADMINISTRATION USER
        // -------------------------------
        Admin admin = new Admin();
        admin.setUsername(normalizedUsername);
        admin.setPassword(encoder.encode(password));
        admin.setRole(AdminRole.ADMINISTRATION);
        admin.setEnabled(true);

        adminRepo.save(admin);

        redirectAttributes.addFlashAttribute(
                "success",
                "Administration user created successfully"
        );

        return "redirect:/admin/users";
    }
}
