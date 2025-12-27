package com.attendance.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
public class RoutineEntryDTO {

    private DayOfWeek day;
    private int period;
    private Long teacherId;
    private String subject;

    public RoutineEntryDTO() {
    }
}
