package com.attendance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor          // REQUIRED for Jackson
@AllArgsConstructor
public class TeacherLoginResponse {

    private boolean success;
    private Long teacherId;
    private String teacherName;
    private String token;    // ✅ JWT TOKEN (CRITICAL)
    private String message;
}
