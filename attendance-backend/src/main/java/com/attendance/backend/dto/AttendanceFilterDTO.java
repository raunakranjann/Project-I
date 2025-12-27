package com.attendance.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceFilterDTO {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private String subject;
    private String teacher;
    private String student;
    private String status;

    private Long courseId;
    private Long branchId;
    private Long academicSessionId;
    private Long semesterId;

    private Integer month;   // 1–12
    private Integer year;
}
