package com.attendance.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class RoutineFormDTO {

    private List<RoutineEntryDTO> entries = new ArrayList<>();

    public List<RoutineEntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<RoutineEntryDTO> entries) {
        this.entries = entries;
    }
}
