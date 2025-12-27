package com.attendance.backend.model.routine.repository;

import com.attendance.backend.model.academics.AcademicSession;
import com.attendance.backend.model.academics.Branch;
import com.attendance.backend.model.academics.Course;
import com.attendance.backend.model.academics.Semester;
import com.attendance.backend.model.routine.WeeklySchedule;
import com.attendance.backend.model.user.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {



    // USED BY ClassSessionService
    List<WeeklySchedule> findByDayOfWeekAndActiveTrue(
            DayOfWeek dayOfWeek
    );
    // =====================================================
    // ROUTINE GRID (ACTIVE SEMESTER)
    // =====================================================
    List<WeeklySchedule>
    findByCourseAndBranchAndAcademicSessionAndSemesterAndActiveTrue(
            Course course,
            Branch branch,
            AcademicSession academicSession,
            Semester semester
    );


    Optional<WeeklySchedule>
    findByCourseAndBranchAndAcademicSessionAndSemesterAndDayOfWeekAndPeriodNo(
            Course course,
            Branch branch,
            AcademicSession academicSession,
            Semester semester,
            DayOfWeek dayOfWeek,
            Integer periodNo
    );

    // =====================================================
    // ❌ OLD (REMOVE OR KEEP UNUSED)
    // Global conflict without semester — NOT REAL WORLD
    // =====================================================
    boolean existsByTeacherAndDayOfWeekAndPeriodNoAndActiveTrue(
            Teacher teacher,
            DayOfWeek dayOfWeek,
            int periodNo
    );

    // =====================================================
    // ✅ FINAL REAL-WORLD TEACHER COLLISION CHECK
    // Same teacher + same semester + same day + same period
    // =====================================================



    boolean existsByTeacherAndSemesterAndDayOfWeekAndPeriodNoAndActiveTrue(
            Teacher teacher,
            Semester semester,
            DayOfWeek dayOfWeek,
            int periodNo
    );
}
