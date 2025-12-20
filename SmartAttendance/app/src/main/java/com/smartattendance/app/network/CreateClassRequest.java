package com.smartattendance.app.network;

public class CreateClassRequest {

    private String subjectName;
    private double latitude;
    private double longitude;
    private double radius;
    private String startTime;
    private String endTime;

    // Required by Gson
    public CreateClassRequest() {
    }

    // ✅ JWT-ONLY constructor (NO teacherId)
    public CreateClassRequest(
            String subjectName,
            double latitude,
            double longitude,
            double radius,
            String startTime,
            String endTime
    ) {
        this.subjectName = subjectName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
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
