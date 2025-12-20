package com.attendance.backend.dto;

public class ClassSessionDto {

    private Long id;
    private String subjectName;
    private String teacherName;
    private double latitude;
    private double longitude;
    private double radius;

    // ✅ REQUIRED CONSTRUCTOR FOR JPQL PROJECTION
    public ClassSessionDto(
            Long id,
            String subjectName,
            String teacherName,
            double latitude,
            double longitude,
            double radius
    ) {
        this.id = id;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }

    // ---------- GETTERS ----------
    public Long getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getTeacherName() {
        return teacherName;
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
}
