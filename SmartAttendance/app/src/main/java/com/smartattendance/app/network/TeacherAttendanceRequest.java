package com.smartattendance.app.network;

public class TeacherAttendanceRequest {

    private Long classId;
    private Long studentId;
    private String status; // PRESENT / ABSENT

    public TeacherAttendanceRequest(Long classId, Long studentId, String status) {
        this.classId = classId;
        this.studentId = studentId;
        this.status = status;
    }

    public Long getClassId() {
        return classId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStatus() {
        return status;
    }
}
