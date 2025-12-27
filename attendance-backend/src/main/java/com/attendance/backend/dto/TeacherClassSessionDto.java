package com.attendance.backend.dto;

import java.time.LocalDateTime;

public record TeacherClassSessionDto(
        Long id,
        String subjectName,
        String courseName,
        String branchName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        boolean active
) {}
