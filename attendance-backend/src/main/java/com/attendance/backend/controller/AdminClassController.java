package com.attendance.backend.controller;

import com.attendance.backend.entity.ClassSession;
import com.attendance.backend.entity.Teacher;
import com.attendance.backend.repository.ClassSessionRepository;
import com.attendance.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/classes")
public class AdminClassController {

    @Autowired
    private ClassSessionRepository classRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    // ===============================
    // SHOW CREATE CLASS PAGE
    // ===============================
    @GetMapping("/new")
    public String showCreateClassPage(Model model) {

        model.addAttribute("teachers", teacherRepo.findAll());
        model.addAttribute("classSession", new ClassSession());

        return "admin/create-class";
    }

    // ===============================
    // LIST ALL CLASSES (ACTIVE + FUTURE + PAST + DELETED)
    // ===============================
    @GetMapping
    public String listClasses(Model model) {

        LocalDateTime now = LocalDateTime.now();

        List<ClassSession> classes =
                classRepo.findAll(
                        Sort.by(
                                Sort.Order.desc("deleted"),
                                Sort.Order.desc("active"),
                                Sort.Order.asc("startTime")
                        )
                );

        // ✅ COMPUTE STATUS HERE (NOT IN THYMELEAF)
        for (ClassSession c : classes) {

            if (c.isDeleted()) {
                c.setStatus("DELETED");

            } else if (now.isBefore(c.getStartTime())) {
                c.setStatus("FUTURE");

            } else if (now.isAfter(c.getEndTime())) {
                c.setStatus("EXPIRED");

            } else {
                c.setStatus("LIVE");
            }
        }

        model.addAttribute("classes", classes);
        return "admin/classes";
    }

    // ===============================
    // HANDLE CLASS CREATION
    // ===============================
    @PostMapping
    public String createClass(
            @RequestParam String subjectName,
            @RequestParam Long teacherId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ClassSession session = new ClassSession();
        session.setSubjectName(subjectName);
        session.setTeacher(teacher);
        session.setLatitude(latitude);
        session.setLongitude(longitude);
        session.setRadius(radius);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        // Scheduler controls lifecycle
        session.setActive(false);
        session.setDeleted(false);

        classRepo.save(session);

        return "redirect:/admin/classes";
    }

    // ===============================
    // SHOW EDIT FORM (FUTURE + LIVE ONLY)
    // ===============================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        ClassSession session = classRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (session.isDeleted()) {
            return "redirect:/admin/classes";
        }

        model.addAttribute("classSession", session);
        model.addAttribute("teachers", teacherRepo.findAll());
        model.addAttribute("selectedTeacherId", session.getTeacher().getId());

        return "admin/edit-class";
    }

    // ===============================
    // HANDLE CLASS UPDATE
    // ===============================
    @PostMapping("/edit")
    public String updateClass(
            @RequestParam Long id,
            @RequestParam String subjectName,
            @RequestParam Long teacherId,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime
    ) {

        ClassSession session = classRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (session.isDeleted()) {
            return "redirect:/admin/classes";
        }

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        session.setSubjectName(subjectName);
        session.setTeacher(teacher);
        session.setLatitude(latitude);
        session.setLongitude(longitude);
        session.setRadius(radius);
        session.setStartTime(startTime);
        session.setEndTime(endTime);

        classRepo.save(session);

        return "redirect:/admin/classes";
    }

    // ===============================
    // DELETE CLASS (ADMIN – ANY TIME)
    // ===============================
    @GetMapping("/delete/{id}")
    public String deleteClass(@PathVariable Long id) {

        ClassSession session = classRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        session.setDeleted(true);
        session.setActive(false);

        classRepo.save(session);

        return "redirect:/admin/classes";
    }
}
