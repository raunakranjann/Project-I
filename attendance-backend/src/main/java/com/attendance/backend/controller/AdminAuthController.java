package com.attendance.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {

    // ROOT URL → ADMIN LOGIN

    @GetMapping("/")
    public String loginPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }


    // OPTIONAL: backward compatibility
    @GetMapping("/admin/login")
    public String adminLoginRedirect() {
        return "redirect:/";
    }
}
