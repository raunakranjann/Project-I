package com.attendance.backend.dto;

import java.time.LocalDateTime;

public class TeacherClassResponse {

    private Long id;
    private String subjectName;
    private double latitude;
    private double longitude;
    private double radius;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TeacherClassResponse(
            Long id,
            String subjectName,
            double latitude,
            double longitude,
            double radius,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.id = id;
        this.subjectName = subjectName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getRadius() { return radius; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
