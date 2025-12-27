package com.attendance.backend.controller.administration;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.repository.AcademicSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administration/academic-sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class AcademicSessionAdminController {

    private final AcademicSessionRepository sessionRepository;

    // ===============================
    // LIST SESSIONS
    // ===============================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("sessions", sessionRepository.findAll());
        return "administration/academic-sessions";
    }

    // ===============================
    // ADD SESSION
    // ===============================
    @PostMapping
    public String save(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear,
            RedirectAttributes redirectAttributes
    ) {

        // -------------------------------
        // BASIC VALIDATION
        // -------------------------------
        if (startYear == null || endYear == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Start year and end year are required"
            );
            return "redirect:/administration/academic-sessions";
        }

        if (endYear <= startYear) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "End year must be greater than start year"
            );
            return "redirect:/administration/academic-sessions";
        }

        // -------------------------------
        // DUPLICATE CHECK
        // -------------------------------
        if (sessionRepository.existsByStartYearAndEndYear(startYear, endYear)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Academic session already exists"
            );
            return "redirect:/administration/academic-sessions";
        }

        // -------------------------------
        // SAVE
        // -------------------------------
        AcademicSession session = new AcademicSession();
        session.setStartYear(startYear);
        session.setEndYear(endYear);
        session.setDisplayName(startYear + "-" + endYear);

        sessionRepository.save(session);

        redirectAttributes.addFlashAttribute(
                "success",
                "Academic session added successfully"
        );

        return "redirect:/administration/academic-sessions";
    }
}
