package com.smartattendance.app.network;

public class StudentModel {

    private Long id;
    private String rollNo;
    private String name;

    /**
     * Values: "PRESENT", "ABSENT", or null
     */
    private String attendanceStatus;

    public Long getId() {
        return id;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getName() {
        return name;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    // ✅ REQUIRED FOR TOGGLE + PERSIST
    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}
