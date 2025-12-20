ANDROID APPLICATION (Smart Attendance App)

This folder contains the Android application for the
Smart Attendance System using Face Recognition and Geo-Fencing.

The Android app is used by Students and Teachers to perform
authentication, attendance marking, and class management using
face recognition and GPS validation.

------------------------------------------------------------

PROJECT DETAILS

Application Name : Smart Attendance App
Package Name     : com.smartattendance.app
Build System     : Gradle
Language         : Java
Compile SDK      : 36
Target SDK       : 36
Minimum SDK      : 24
Version Code     : 1
Version Name     : 1.0
Java Version     : 11

------------------------------------------------------------

PURPOSE IN MAIN PROJECT

The Android application is responsible for:

- Student and Teacher login
- Role-based dashboard navigation
- Capturing live face images using CameraX
- Fetching GPS location for geo-fencing validation
- Sending attendance requests to the backend
- Displaying attendance and class information
- Maintaining login session using local storage

------------------------------------------------------------

TECHNOLOGIES AND LIBRARIES USED

- Java (Android)
- Android SDK
- Material Design Components
- Retrofit (REST API communication)
- Gson (JSON parsing)
- OkHttp Logging Interceptor
- Google Play Services Location (GPS)
- CameraX (Camera functionality)
- ML Kit Face Detection
- RecyclerView
- CardView

------------------------------------------------------------

CORE FUNCTIONALITIES

- Role-based login (Student / Teacher)
- Persistent login using SharedPreferences
- Face capture using CameraX
- Face detection using ML Kit
- GPS-based geo-fencing validation
- Class-wise attendance marking
- Attendance uniqueness enforcement:
  (Student + Class + Date)
- Teacher class management and dashboards

------------------------------------------------------------

APPLICATION STRUCTURE

android-app/
|
|-- activities/
|-- adapters/
|-- models/
|-- network/
|-- utils/
|-- res/
|   |-- layout/
|   |-- drawable/
|   |-- values/
|-- AndroidManifest.xml

------------------------------------------------------------

PERMISSIONS USED

The application requires the following permissions:

- Camera
- Location (GPS)
- Internet
- Network State

Permissions are requested at runtime where required.

------------------------------------------------------------

HOW TO RUN

1. Open the Android project in Android Studio
2. Ensure Android SDK 36 and Java 11 are configured
3. Update backend API base URL in network configuration
4. Connect a physical Android device or start an emulator
5. Grant camera and location permissions
6. Run the application

------------------------------------------------------------

INTEGRATION

- Communicates with the Spring Boot backend using REST APIs
- Uses AI-SERVICE for face verification during attendance marking
- GPS data is validated before attendance submission

------------------------------------------------------------

IMPLEMENTATION NOTES

- CameraX is used for reliable camera handling
- ML Kit performs on-device face detection
- Attendance is allowed only within valid geo-fence
- Student and Teacher workflows are fully separated
- Designed for academic and internal project usage

------------------------------------------------------------

AUTHOR

Raunak Ranjan
B.Tech – Computer Science & Engineering
