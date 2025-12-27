package com.attendance.backend.service;

import com.attendance.backend.model.routine.ClassSession;
import com.attendance.backend.model.routine.ScheduleConfig;
import com.attendance.backend.model.routine.WeeklySchedule;
import com.attendance.backend.model.routine.repository.ClassSessionRepository;
import com.attendance.backend.model.routine.repository.ScheduleConfigRepository;
import com.attendance.backend.model.routine.repository.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassSessionService {

    private final WeeklyScheduleRepository weeklyScheduleRepo;
    private final ScheduleConfigRepository scheduleConfigRepo;
    private final ClassSessionRepository classSessionRepo;

    @Transactional
    public void regenerateClassesForDate(LocalDate date) {

        // 🔥 REMOVE existing generated sessions for the day
        classSessionRepo.deleteBySessionDate(date);

        // ♻️ Re-generate from updated routine
        generateClassesForDate(date);
    }



    @Transactional
    public void generateClassesForDate(LocalDate date) {

        DayOfWeek day = date.getDayOfWeek();

        ScheduleConfig config = scheduleConfigRepo.findByActiveTrue()
                .orElseThrow(() ->
                        new IllegalStateException("Active ScheduleConfig not found")
                );

        List<WeeklySchedule> routines =
                weeklyScheduleRepo.findByDayOfWeekAndActiveTrue(day);

        for (WeeklySchedule routine : routines) {

            if (routine.getSubjectName() == null || routine.getSubjectName().isBlank())
                continue;

            // HARD GUARD — REQUIRED
            if (routine.getTeacher() == null) {
                continue;
            }

            LocalTime startTime =
                    config.getStartTimeForPeriod(routine.getPeriodNo());
            LocalTime endTime =
                    config.getEndTimeForPeriod(routine.getPeriodNo());

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            boolean exists =
                    classSessionRepo.existsByCourseAndBranchAndAcademicSessionAndStartTime(
                            routine.getCourse(),
                            routine.getBranch(),
                            routine.getAcademicSession(),
                            startDateTime
                    );

            if (exists) continue;

            ClassSession session = new ClassSession();
            session.setSubjectName(routine.getSubjectName());
            session.setTeacher(routine.getTeacher());
            session.setCourse(routine.getCourse());
            session.setBranch(routine.getBranch());
            session.setAcademicSession(routine.getAcademicSession());

            session.setSessionDate(date);
            session.setStartTime(startDateTime);
            session.setEndTime(endDateTime);
            session.setActive(false);
            session.setDeleted(false);

            classSessionRepo.save(session);
        }
    }
}
