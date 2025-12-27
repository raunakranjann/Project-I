package com.attendance.backend.controller.administration;

import com.attendance.backend.dto.BranchDto;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.repository.BranchRepository;
import com.attendance.backend.model.academics.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/administration/branches")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class BranchAdminController {

    private final BranchRepository branchRepository;
    private final CourseRepository courseRepository;

    // =================================
    // LIST BRANCHES (PAGE)
    // =================================
    @GetMapping
    public String list(Model model) {
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        return "administration/branches";
    }

    // =================================
    // 🔥 API: BRANCHES BY COURSE (DTO)
    // SAFE FOR JSON
    // =================================
    @GetMapping("/by-course")
    @ResponseBody
    public List<BranchDto> getBranchesByCourse(
            @RequestParam Long courseId
    ) {
        return branchRepository.findByCourseId(courseId)
                .stream()
                .map(b -> new BranchDto(b.getId(), b.getCode()))
                .toList();
    }

    // =================================
    // ADD BRANCH
    // =================================
    @PostMapping
    public String save(
            @RequestParam String code,
            @RequestParam String name,
            @RequestParam Long courseId,
            RedirectAttributes redirectAttributes
    ) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course"));

        String normalizedCode = code.trim().toUpperCase();
        String normalizedName = name.trim();

        if (branchRepository.existsByCodeAndCourseId(normalizedCode, courseId)) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Branch already exists for selected course"
            );
            return "redirect:/administration/branches";
        }

        Branch branch = new Branch();
        branch.setCode(normalizedCode);
        branch.setName(normalizedName);
        branch.setCourse(course);

        branchRepository.save(branch);

        redirectAttributes.addFlashAttribute(
                "success",
                "Branch added successfully"
        );

        return "redirect:/administration/branches";
    }
}
