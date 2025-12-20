#SMART ATTENDANCE SYSTEM

##Using Face Recognition and Geo-Fencing

This repository contains the complete implementation of the
Smart Attendance System developed as a B.Tech Final Year Project.

The system automates attendance marking by combining an Android
application, a Spring Boot backend, and an AI-based face
verification service.

------------------------------------------------------------

PROJECT MODULES

The project is divided into three independent modules:

1. ANDROID APPLICATION
2. BACKEND SERVICE
3. AI-SERVICE (Face Verification)

Each module is developed and maintained separately and communicates
with others through well-defined APIs.

------------------------------------------------------------

ANDROID APPLICATION (SmartAttendance)

Folder Name:
SmartAttendance

Purpose:
The Android application is used by Students and Teachers to mark
attendance and manage classes.

Responsibilities:
- Student and Teacher login
- Role-based dashboard navigation
- Live face capture using CameraX
- Face detection using ML Kit
- GPS-based geo-fencing validation
- Attendance marking and viewing
- Communication with backend REST APIs

Detailed documentation:
SmartAttendance/README.txt

------------------------------------------------------------

BACKEND SERVICE (Spring Boot)

Folder Name:
attendance-backend

Purpose:
The backend service acts as the central system controller and
manages authentication, attendance records, and data storage.

Responsibilities:
- Student, Teacher, and Admin authentication
- Class and session management
- Attendance validation and storage
- Attendance report generation
- Integration with AI face verification service
- Admin dashboard using Thymeleaf

Technologies:
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Thymeleaf

Detailed documentation:
attendance-backend/README.txt

------------------------------------------------------------

AI-SERVICE (Face Verification)

Folder Name:
ai-service

Purpose:
The AI-SERVICE verifies a student's identity by comparing a
registered face image with a live captured image.

Responsibilities:
- Face comparison using AI models
- Identity verification during attendance marking
- Returning verification result to backend

Technologies:
- Python 3.10
- FastAPI
- face_recognition
- NumPy

Detailed documentation:
ai-service/README.txt

------------------------------------------------------------

SYSTEM WORKFLOW

1. Student opens the Android application
2. Student logs in and selects a class
3. Live face image and GPS location are captured
4. Android app sends data to backend
5. Backend calls AI-SERVICE for face verification
6. Backend validates geo-fence and face match
7. Attendance is stored in the database

------------------------------------------------------------

PROJECT STRUCTURE

Smart-Attendance-System/
|
|-- SmartAttendance/        (Android Application)
|-- attendance-backend/     (Spring Boot Backend)
|-- ai-service/             (AI Face Verification)
|-- README.txt              (This file)

------------------------------------------------------------

ACADEMIC INFORMATION

Project Type : B.Tech Final Year Project
Domain       : Android, Backend, Artificial Intelligence
Focus Areas  : Face Recognition, Geo-Fencing, Secure Attendance

------------------------------------------------------------

AUTHOR

Raunak Ranjan
B.Tech – Computer Science & Engineering

------------------------------------------------------------

NOTES

- Each module can be run independently
- Configuration details are available in respective module READMEs
- The project is designed strictly for academic and internal usage
