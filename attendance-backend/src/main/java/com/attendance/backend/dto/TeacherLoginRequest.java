package com.attendance.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherLoginRequest {
    private String username;
    private String password;
}
