package com.attendance.backend.controller.report;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import com.attendance.backend.model.academics.repository.AcademicSessionRepository;
import com.attendance.backend.model.academics.repository.BranchRepository;
import com.attendance.backend.model.academics.repository.CourseRepository;
import com.attendance.backend.model.academics.repository.SemesterRepository;
import com.attendance.backend.model.routine.ScheduleConfig;
import com.attendance.backend.model.routine.WeeklySchedule;
import com.attendance.backend.model.routine.repository.ScheduleConfigRepository;
import com.attendance.backend.model.routine.repository.WeeklyScheduleRepository;
import com.attendance.backend.service.RoutinePdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/administration/routine")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RoutinePdfController {

    private final WeeklyScheduleRepository routineRepo;
    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final SemesterRepository semesterRepo;
    private final ScheduleConfigRepository periodConfigRepo;
    private final RoutinePdfService pdfService;

    // =====================================================
    // DOWNLOAD ROUTINE PDF (AUTO ACTIVE SEMESTER)
    // =====================================================
    @GetMapping(value = "/pdf", produces = "application/pdf")
    public void downloadRoutinePdf(
            @RequestParam Long courseId,
            @RequestParam Long branchId,
            @RequestParam Long academicSessionId,
            HttpServletResponse response
    ) throws Exception {

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Branch branch = branchRepo.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        AcademicSession session = sessionRepo.findById(academicSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Academic session not found"));

        // =================================================
        // 🔥 RESOLVE ACTIVE SEMESTER (SINGLE SOURCE OF TRUTH)
        // =================================================
        Semester semester = semesterRepo
                .findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
                        courseId, branchId, academicSessionId
                )
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No active semester found")
                );

        // =================================================
        // FETCH ROUTINE
        // =================================================
        List<WeeklySchedule> routines =
                routineRepo.findByCourseAndBranchAndAcademicSessionAndSemesterAndActiveTrue(
                        course, branch, session, semester
                );

        int totalPeriods = periodConfigRepo
                .findByActiveTrue()
                .map(ScheduleConfig::getTotalPeriods)
                .orElse(7);

        // =================================================
        // RESPONSE HEADERS
        // =================================================
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=routine_semester_" + semester.getNumber() + ".pdf"
        );

        // =================================================
        // GENERATE GRID PDF
        // =================================================
        pdfService.generateRoutinePdf(
                routines,
                totalPeriods,
                response.getOutputStream()
        );
    }
}
