package com.attendance.backend.controller.administration;

import com.attendance.backend.model.routine.ScheduleConfig;
import com.attendance.backend.model.routine.repository.ScheduleConfigRepository;
import com.attendance.backend.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;

@Controller
@RequestMapping("/administration/periods")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class AdministrationPeriodController {

    private final ScheduleConfigRepository repo;
    private final TimeslotService timeslotService;

    // =====================================================
    // VIEW CURRENT PERIOD CONFIG
    // =====================================================
    @GetMapping
    public String view(Model model) {

        repo.findByActiveTrue()
                .ifPresent(config -> model.addAttribute("config", config));

        return "administration/periods";
    }

    // =====================================================
    // SAVE / UPDATE PERIOD CONFIG (SAFE + FINAL)
    // =====================================================
    @PostMapping
    public String save(
            @RequestParam int totalPeriods,
            @RequestParam int durationMinutes,
            @RequestParam LocalTime dayStartTime,
            RedirectAttributes redirect
    ) {

        // -------------------------------
        // VALIDATION
        // -------------------------------
        if (totalPeriods < 1 || totalPeriods > 12) {
            redirect.addFlashAttribute(
                    "error",
                    "Total periods must be between 1 and 12"
            );
            return "redirect:/administration/periods";
        }

        if (durationMinutes < 30 || durationMinutes > 180) {
            redirect.addFlashAttribute(
                    "error",
                    "Period duration must be between 30 and 180 minutes"
            );
            return "redirect:/administration/periods";
        }

        if (dayStartTime == null) {
            redirect.addFlashAttribute(
                    "error",
                    "Day start time is required"
            );
            return "redirect:/administration/periods";
        }

        // -------------------------------
        // DEACTIVATE OLD CONFIG (DO NOT DELETE)
        // -------------------------------
        repo.findByActiveTrue().ifPresent(existing -> {
            existing.setActive(false);
            repo.save(existing);
        });

        // -------------------------------
        // CREATE NEW ACTIVE CONFIG
        // -------------------------------
        ScheduleConfig config = new ScheduleConfig();
        config.setTotalPeriods(totalPeriods);
        config.setDurationMinutes(durationMinutes);
        config.setDayStartTime(dayStartTime);
        config.setActive(true);

        ScheduleConfig savedConfig = repo.save(config);

        // -------------------------------
        // 🔥 SAFE TIMESLOT REGENERATION
        // (NO DELETE, NO FK BREAK)
        // -------------------------------
        timeslotService.regenerateTimeslots(savedConfig);

        redirect.addFlashAttribute(
                "success",
                "Period configuration updated successfully"
        );

        return "redirect:/administration/periods";
    }
}
