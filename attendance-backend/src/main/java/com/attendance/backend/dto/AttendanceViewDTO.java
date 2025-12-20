package com.attendance.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceViewDTO(
        Long userId,
        String rollNo,
        String userName,
        Long classId,
        String subjectName,
        String teacherName,
        LocalDate date,
        LocalDateTime timestamp,
        String status
) {}
