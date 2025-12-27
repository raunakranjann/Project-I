package com.attendance.backend.controller.administration;

import com.attendance.backend.dto.RoutineEntryDTO;
import com.attendance.backend.dto.RoutineFormDTO;
import com.attendance.backend.model.academics.*;
import com.attendance.backend.model.academics.repository.*;
import com.attendance.backend.model.routine.ScheduleConfig;
import com.attendance.backend.model.routine.Timeslot;
import com.attendance.backend.model.routine.WeeklySchedule;
import com.attendance.backend.model.routine.repository.ScheduleConfigRepository;
import com.attendance.backend.model.routine.repository.TimeslotRepository;
import com.attendance.backend.model.routine.repository.WeeklyScheduleRepository;
import com.attendance.backend.model.user.Teacher;
import com.attendance.backend.model.user.repository.TeacherRepository;
import com.attendance.backend.service.ClassSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/administration/routine")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATION')")
public class AdministrationRoutineController {

    private final ClassSessionService classSessionService;
    private final WeeklyScheduleRepository routineRepo;
    private final CourseRepository courseRepo;
    private final BranchRepository branchRepo;
    private final AcademicSessionRepository sessionRepo;
    private final SemesterRepository semesterRepo;
    private final TeacherRepository teacherRepo;
    private final ScheduleConfigRepository periodConfigRepo;
    private final TimeslotRepository timeslotRepo;

    // =====================================================
    // ROUTINE BUILDER PAGE
    // =====================================================
    @GetMapping
    public String view(Model model) {

        int totalPeriods = periodConfigRepo
                .findByActiveTrue()
                .map(ScheduleConfig::getTotalPeriods)
                .orElse(7);

        model.addAttribute("courses", courseRepo.findAll());
        model.addAttribute("sessions", sessionRepo.findAll());
        model.addAttribute("days", DayOfWeek.values());
        model.addAttribute("totalPeriods", totalPeriods);
        model.addAttribute("timeslots", timeslotRepo.findByActiveTrueOrderByPeriodNoAsc());

        return "administration/routine";
    }

    // =====================================================
    // AJAX: BRANCHES BY COURSE
    // =====================================================
    @GetMapping("/branches-by-course")
    @ResponseBody
    public List<Map<String, Object>> branchesByCourse(@RequestParam Long courseId) {

        return branchRepo.findByCourseId(courseId)
                .stream()
                .map(b -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", b.getId());
                    m.put("code", b.getCode());
                    return m;
                })
                .toList();
    }

    // =====================================================
    // LOAD ROUTINE (SUBJECT ALWAYS, TEACHER OPTIONAL)
    // =====================================================
    @GetMapping("/load")
    @ResponseBody
    public List<Map<String, Object>> loadRoutine(
            @RequestParam Long courseId,
            @RequestParam Long branchId,
            @RequestParam Long academicSessionId
    ) {

        Course course = courseRepo.findById(courseId).orElseThrow();
        Branch branch = branchRepo.findById(branchId).orElseThrow();
        AcademicSession session = sessionRepo.findById(academicSessionId).orElseThrow();

        return semesterRepo
                .findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
                        courseId, branchId, academicSessionId
                )
                .map(semester ->
                        routineRepo
                                .findByCourseAndBranchAndAcademicSessionAndSemesterAndActiveTrue(
                                        course, branch, session, semester
                                )
                                .stream()
                                .map(r -> {
                                    Map<String, Object> m = new HashMap<>();
                                    m.put("dayOfWeek", r.getDayOfWeek().name());
                                    m.put("periodNo", r.getPeriodNo());
                                    m.put("subjectName", r.getSubjectName());

                                    if (r.getTeacher() != null) {
                                        Map<String, Object> t = new HashMap<>();
                                        t.put("id", r.getTeacher().getId());
                                        t.put("name", r.getTeacher().getName());
                                        m.put("teacher", t);
                                    }

                                    return m;
                                })
                                .toList()
                )
                .orElse(List.of());
    }

    // =====================================================
    // AVAILABLE TEACHERS (BUSY CHECK)
    // =====================================================
    @GetMapping("/available-teachers")
    @ResponseBody
    public List<Map<String, Object>> availableTeachers(
            @RequestParam String day,
            @RequestParam int period
    ) {

        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());

        return teacherRepo.findAll()
                .stream()
                .map(t -> {
                    boolean busy =
                            routineRepo.existsByTeacherAndDayOfWeekAndPeriodNoAndActiveTrue(
                                    t, dayOfWeek, period
                            );

                    Map<String, Object> m = new HashMap<>();
                    m.put("id", t.getId());
                    m.put("name", t.getName());
                    m.put("busy", busy);
                    return m;
                })
                .toList();
    }

    // =====================================================
    // APPLY ROUTINE → GENERATE TODAY (MANUAL BUTTON)
    // =====================================================
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMINISTRATION')")
    public String generateTodayFromRoutine(RedirectAttributes redirect) {

        classSessionService.regenerateClassesForDate(LocalDate.now());


        redirect.addFlashAttribute(
                "success",
                "Routine applied successfully for today"
        );
        return "redirect:/administration/routine";
    }

    // =====================================================
    // SAVE / UPDATE ROUTINE (SUBJECT REQUIRED, TEACHER OPTIONAL)
    // =====================================================
    @PostMapping
    @Transactional
    public String save(
            @RequestParam Long courseId,
            @RequestParam Long branchId,
            @RequestParam Long academicSessionId,
            @ModelAttribute RoutineFormDTO form,
            RedirectAttributes redirect
    ) {

        Course course = courseRepo.findById(courseId).orElseThrow();
        Branch branch = branchRepo.findById(branchId).orElseThrow();
        AcademicSession session = sessionRepo.findById(academicSessionId).orElseThrow();

        Semester semester = semesterRepo
                .findByCourseIdAndBranchIdAndAcademicSessionIdAndActiveTrue(
                        courseId, branchId, academicSessionId
                )
                .orElseThrow(() -> new IllegalStateException("No active semester"));

        // ===============================
        // 2️⃣ 🔥 DEACTIVATE OLD ROUTINE
        // ===============================
        routineRepo
                .findByCourseAndBranchAndAcademicSessionAndSemesterAndActiveTrue(
                        course, branch, session, semester
                )
                .forEach(r -> {
                    r.setActive(false);
                    routineRepo.save(r);
                });



        for (RoutineEntryDTO e : form.getEntries()) {

            // SUBJECT IS REQUIRED
            if (e.getSubject() == null || e.getSubject().isBlank()) continue;

            Teacher teacher = null;
            if (e.getTeacherId() != null) {
                teacher = teacherRepo.findById(e.getTeacherId()).orElseThrow();
            }

            final Teacher finalTeacher = teacher;

            // TEACHER BUSY CHECK (ONLY IF SELECTED)
            if (finalTeacher != null) {

                boolean teacherBusy =
                        routineRepo.existsByTeacherAndSemesterAndDayOfWeekAndPeriodNoAndActiveTrue(
                                finalTeacher, semester, e.getDay(), e.getPeriod()
                        )
                                &&
                                routineRepo
                                        .findByCourseAndBranchAndAcademicSessionAndSemesterAndDayOfWeekAndPeriodNo(
                                                course, branch, session, semester,
                                                e.getDay(), e.getPeriod()
                                        )
                                        .map(r -> !finalTeacher.equals(r.getTeacher()))
                                        .orElse(true);

                if (teacherBusy) {
                    redirect.addFlashAttribute(
                            "error",
                            "Teacher " + finalTeacher.getName()
                                    + " already has a class on "
                                    + e.getDay() + " (Period " + e.getPeriod() + ")"
                    );
                    return "redirect:/administration/routine";
                }
            }

            Timeslot timeslot = timeslotRepo
                    .findByPeriodNoAndActiveTrue(e.getPeriod())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Active timeslot not found for period " + e.getPeriod()
                            )
                    );


            WeeklySchedule routine =
                    routineRepo
                            .findByCourseAndBranchAndAcademicSessionAndSemesterAndDayOfWeekAndPeriodNo(
                                    course, branch, session, semester,
                                    e.getDay(), e.getPeriod()
                            )
                            .orElseGet(() -> {
                                WeeklySchedule nr = new WeeklySchedule();
                                nr.setCourse(course);
                                nr.setBranch(branch);
                                nr.setAcademicSession(session);
                                nr.setSemester(semester);
                                nr.setDayOfWeek(e.getDay());
                                nr.setPeriodNo(e.getPeriod());
                                nr.setTimeslot(timeslot);
                                return nr;
                            });

            routine.setTeacher(teacher);          // OPTIONAL ✔
            routine.setSubjectName(e.getSubject());
            routine.setTimeslot(timeslot);        // REQUIRED ✔
            routine.setActive(true);

            routineRepo.save(routine);
        }

        redirect.addFlashAttribute("success", "Weekly routine saved successfully");
        return "redirect:/administration/routine";
    }
}
