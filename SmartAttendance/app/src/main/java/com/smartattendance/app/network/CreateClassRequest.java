package com.smartattendance.app.network;

public class CreateClassRequest {

    private Long teacherId;
    private String subjectName;
    private double latitude;
    private double longitude;
    private double radius;
    private String startTime;   // ISO_LOCAL_DATE_TIME
    private String endTime;     // ISO_LOCAL_DATE_TIME

    // ✅ Required by Gson
    public CreateClassRequest() {
    }

    // ✅ Used when sending request
    public CreateClassRequest(
            Long teacherId,
            String subjectName,
            double latitude,
            double longitude,
            double radius,
            String startTime,
            String endTime
    ) {
        this.teacherId = teacherId;
        this.subjectName = subjectName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // -------- Getters (important for debugging & reuse) --------
    public Long getTeacherId() {
        return teacherId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRadius() {
        return radius;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
