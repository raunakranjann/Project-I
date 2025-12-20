package com.smartattendance.app.network;

public class TeacherLoginResponse {

    private String status;
    private String message;
    private Long teacherId;
    private String teacherName;

    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }
}
