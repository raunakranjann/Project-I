package com.attendance.backend.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {

    // =====================================================
    // ROOT → REDIRECT TO LOGIN OR DASHBOARD
    // =====================================================
    @GetMapping("/")
    public String root(HttpServletRequest request) {

        // If already logged in (session exists)
        if (request.getUserPrincipal() != null) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/admin/login";
    }

    // =====================================================
    // ADMIN LOGIN PAGE
    // =====================================================
    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin/login";
    }
}
