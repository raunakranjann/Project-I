BACKEND SERVICE (Spring Boot)

This folder contains the Spring Boot backend for the
Smart Attendance System using Face Recognition and Geo-Fencing.

The backend is developed as part of a B.Tech Final Year project and
handles authentication, class management, attendance processing, and
integration with the AI face verification service.

------------------------------------------------------------

PROJECT DETAILS

Project Name   : attendance-backend
Group ID       : com.attendance
Version        : 0.0.1-SNAPSHOT
Build Tool     : Maven
Java Version   : 17
Spring Boot    : 3.4.12

------------------------------------------------------------

PURPOSE IN MAIN PROJECT

The backend service is responsible for:

- Handling Student and Teacher authentication
- Managing class sessions created by teachers
- Validating attendance requests from the Android application
- Storing attendance records in the database
- Communicating with the AI-SERVICE for face verification
- Providing data for the Admin Dashboard (Thymeleaf)

------------------------------------------------------------

TECHNOLOGIES USED

- Java 17
- Spring Boot
- Spring Web (REST APIs)
- Spring Data JPA (Hibernate)
- Spring Security
- Thymeleaf (Admin Dashboard)
- PostgreSQL
- Lombok

------------------------------------------------------------

CORE FUNCTIONALITIES

- Role-based authentication (Student / Teacher / Admin)
- Secure backend using Spring Security
- Class creation and management by teachers
- Attendance marking and validation
- Attendance uniqueness enforcement:
  (Student + Class + Date)
- Attendance report generation
- Admin dashboard support using Thymeleaf

------------------------------------------------------------

PROJECT STRUCTURE

backend/
|
|-- controller/
|-- service/
|-- repository/
|-- entity/
|-- dto/
|-- templates/        (Thymeleaf views)
|-- application.properties
|-- AttendanceBackendApplication.java

------------------------------------------------------------

DATABASE CONFIGURATION

The backend uses PostgreSQL as the database.

Database configuration is defined in:

application.properties

Example:

spring.datasource.url=jdbc:postgresql://localhost:5432/smart_attendance
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

------------------------------------------------------------

SECURITY

- Spring Security is used for backend protection
- Authentication and authorization are handled server-side
- Role-based access is enforced for Student, Teacher, and Admin modules

------------------------------------------------------------

HOW TO RUN

1. Open the backend project in IntelliJ IDEA or Eclipse
2. Ensure Java 17 is installed and configured
3. Update PostgreSQL credentials in application.properties
4. Ensure PostgreSQL server is running
5. Run the Spring Boot application using Maven or IDE

Backend runs at:
http://localhost:8080

------------------------------------------------------------

INTEGRATION

- Android application communicates with this backend using REST APIs
- AI-SERVICE is called during attendance marking for face verification
- Admin dashboard is accessed using Thymeleaf templates

------------------------------------------------------------

IMPLEMENTATION NOTES

- Teacher and Student flows are handled separately
- Multiple classes per teacher are supported
- Secure session handling using Spring Security
- Designed strictly for academic and internal project usage

------------------------------------------------------------

AUTHOR

Raunak Ranjan
B.Tech – Computer Science & Engineering
