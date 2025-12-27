package com.attendance.backend.controller.administration;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import com.attendance.backend.model.academics.repository.AcademicSessionRepository;
import com.attendance.backend.model.academics.repository.BranchRepository;
import com.attendance.backend.model.academics.repository.CourseRepository;
import com.attendance.backend.model.academics.repository.SemesterRepository;
import com.attendance.backend.model.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/administration/semesters")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class SemesterManagementController {

    private final SemesterRepository semesterRepo;
    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final StudentRepository studentRepo;



    // =====================================================
    // LIST + CREATE FORM
    // =====================================================
    @GetMapping
    public String view(Model model) {

        model.addAttribute("semesters", semesterRepo.findAll());
        model.addAttribute("courses", courseRepo.findAll());
        model.addAttribute("branches", branchRepo.findAll());
        model.addAttribute("sessions", sessionRepo.findAll());

        return "administration/semesters";
    }

    // =====================================================
    // CREATE SEMESTER (INACTIVE BY DEFAULT)
    // =====================================================
    @PostMapping
    public String create(
            @RequestParam Long courseId,
            @RequestParam Long branchId,
            @RequestParam Long academicSessionId,
            @RequestParam int number,
            RedirectAttributes redirect
    ) {

        if (number <= 0) {
            redirect.addFlashAttribute(
                    "error",
                    "Semester number must be greater than zero"
            );
            return "redirect:/administration/semesters";
        }

        if (semesterRepo.existsByCourseIdAndBranchIdAndAcademicSessionIdAndNumber(
                courseId, branchId, academicSessionId, number)) {

            redirect.addFlashAttribute(
                    "error",
                    "Semester already exists for selected context"
            );
            return "redirect:/administration/semesters";
        }

        Semester semester = new Semester();
        semester.setCourse(courseRepo.findById(courseId).orElseThrow());
        semester.setBranch(branchRepo.findById(branchId).orElseThrow());
        semester.setAcademicSession(sessionRepo.findById(academicSessionId).orElseThrow());
        semester.setNumber(number);
        semester.setActive(false); // 🔒 inactive by default

        semesterRepo.save(semester);

        redirect.addFlashAttribute(
                "success",
                "Semester created successfully (inactive)"
        );

        return "redirect:/administration/semesters";
    }

    // =====================================================
    // ACTIVATE SEMESTER (AUTO ROLLOVER)
    // =====================================================
    @PostMapping("/{id}/activate")
    @Transactional
    public String activateSemester(
            @PathVariable Long id,
            RedirectAttributes redirect
    ) {

        Semester newSemester = semesterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found"));

        Course course = newSemester.getCourse();
        Branch branch = newSemester.getBranch();
        AcademicSession session = newSemester.getAcademicSession();

        // -------------------------------------------------
        // 1️⃣ Deactivate existing active semester (if exists)
        // -------------------------------------------------
        semesterRepo
                .findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
                        course.getId(),
                        branch.getId(),
                        session.getId()
                )
                .ifPresent(old -> {
                    old.setActive(false);
                    semesterRepo.save(old);
                });

        // -------------------------------------------------
        // 2️⃣ Activate new semester
        // -------------------------------------------------
        newSemester.setActive(true);
        semesterRepo.save(newSemester);

        // -------------------------------------------------
        // 3️⃣ Promote students → attach to new semester
        // -------------------------------------------------
        studentRepo
                .findByCourseAndBranchAndAcademicSession(
                        course, branch, session
                )
                .forEach(student -> {
                    student.setSemester(newSemester);
                    studentRepo.save(student);
                });

        redirect.addFlashAttribute(
                "success",
                "Semester activated. Students moved to Semester "
                        + newSemester.getNumber()
        );

        return "redirect:/administration/semesters";
    }
}
