package com.attendance.backend.dto;

public record TeacherAvailabilityDto(
        Long id,
        String name,
        boolean busy
) {}
