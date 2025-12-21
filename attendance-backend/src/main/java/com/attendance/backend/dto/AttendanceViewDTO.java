package com.attendance.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface-based projection for native query results.
 * Required for Hibernate 6 + PostgreSQL compatibility.
 */
public interface AttendanceViewDTO {

    Long getUserId();

    String getRollNo();

    String getUserName();

    Long getClassId();

    String getSubjectName();

    String getTeacherName();

    LocalDate getDate();

    LocalDateTime getTimestamp();

    String getStatus();
}
