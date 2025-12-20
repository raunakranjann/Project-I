package com.smartattendance.app.network;
public class StudentLoginRequest {
    private String rollNo;
    private String password;
    private String deviceId;

    public StudentLoginRequest(String rollNo, String password, String deviceId) {
        this.rollNo = rollNo;
        this.password = password;
        this.deviceId = deviceId;
    }

    public String getRollNo() { return rollNo; }
    public String getPassword() { return password; }
    public String getDeviceId() { return deviceId; }
}
