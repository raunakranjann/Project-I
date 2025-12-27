package com.attendance.backend.controller.administration;

import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administration/courses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class CourseAdminController {

    private final CourseRepository courseRepository;

    // ================================
    // LIST COURSES
    // ================================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "administration/courses";
    }

    // ================================
    // ADD COURSE
    // ================================
    @PostMapping
    public String save(
            @ModelAttribute Course course,
            RedirectAttributes redirectAttributes
    ) {

        String normalizedName = course.getName().trim().toUpperCase();
        course.setName(normalizedName);

        if (courseRepository.existsByName(normalizedName)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Course already exists"
            );
            return "redirect:/administration/courses";
        }

        courseRepository.save(course);

        redirectAttributes.addFlashAttribute(
                "success",
                "Course added successfully"
        );

        return "redirect:/administration/courses";
    }
}
