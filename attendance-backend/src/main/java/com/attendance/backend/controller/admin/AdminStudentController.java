package com.attendance.backend.controller.admin;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import com.attendance.backend.model.academics.repository.AcademicSessionRepository;
import com.attendance.backend.model.academics.repository.BranchRepository;
import com.attendance.backend.model.academics.repository.CourseRepository;
import com.attendance.backend.model.academics.repository.SemesterRepository;
import com.attendance.backend.model.user.Student;
import com.attendance.backend.model.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
@Controller
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private static final String USER_IMAGE_DIR =
            "D:/smart-attendance/uploads/users";

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final SemesterRepository semesterRepo;
    private final PasswordEncoder passwordEncoder;

    // ================================
    // SHOW REGISTER PAGE
    // ================================
    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute("courses", courseRepo.findAll());
        model.addAttribute("branches", branchRepo.findAll());
        model.addAttribute("sessions", sessionRepo.findAll());

        return "admin/register-student";
    }

    // ================================
    // REGISTER STUDENT (ACTIVE SEMESTER ONLY)
    // ================================
    @PostMapping("/register")
    public String registerStudent(
            @RequestParam String name,
            @RequestParam String rollNo,
            @RequestParam String password,
            @RequestParam Long courseId,
            @RequestParam Long branchId,
            @RequestParam Long academicSessionId,
            @RequestParam("photo") MultipartFile photo,
            RedirectAttributes redirect
    ) {

        try {
            // --------------------------------
            // DUPLICATE CHECK
            // --------------------------------
            if (studentRepo.findByRollNo(rollNo).isPresent()) {
                redirect.addFlashAttribute(
                        "error",
                        "Roll number already exists"
                );
                return "redirect:/admin/students/register";
            }

            // --------------------------------
            // LOAD CONTEXT
            // --------------------------------
            Course course = courseRepo.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid course"));

            Branch branch = branchRepo.findById(branchId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid branch"));

            AcademicSession session = sessionRepo.findById(academicSessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid academic session"));

            // 🔒 ACTIVE SEMESTER ONLY
            Semester semester = semesterRepo
                    .findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
                            courseId, branchId, academicSessionId
                    )
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No active semester found. Activate a semester first."
                            )
                    );

            // --------------------------------
            // SAVE PHOTO
            // --------------------------------
            File dir = new File(USER_IMAGE_DIR);
            if (!dir.exists()) dir.mkdirs();

            String imagePath = USER_IMAGE_DIR + "/" + rollNo + ".jpg";
            photo.transferTo(new File(imagePath));

            // --------------------------------
            // SAVE STUDENT
            // --------------------------------
            Student student = new Student();
            student.setName(name.trim());
            student.setRollNo(rollNo.trim());
            student.setPassword(passwordEncoder.encode(password));
            student.setPhotoUrl(imagePath);
            student.setDeviceId(null);

            student.setCourse(course);
            student.setBranch(branch);
            student.setAcademicSession(session);
            student.setSemester(semester);   // 🔥 SYSTEM CONTROLLED

            studentRepo.save(student);

            redirect.addFlashAttribute(
                    "success",
                    "Student registered in Semester " + semester.getNumber()
            );

            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/students/register";
        }
    }
}
