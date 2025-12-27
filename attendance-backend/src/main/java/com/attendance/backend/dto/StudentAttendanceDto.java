package com.attendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentAttendanceDto {

    private Long id;
    private String rollNo;
    private String name;

    /**
     * PRESENT / ABSENT / null
     */
    private String attendanceStatus;
}
