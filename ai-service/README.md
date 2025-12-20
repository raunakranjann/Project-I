#AI-SERVICE (Face Verification)

This folder contains the AI face verification service used in the
Smart Attendance System using Face Recognition and Geo-Fencing.

The service is responsible for verifying a student's identity by
comparing a registered face image with a live captured image during
attendance marking.

------------------------------------------------------------

PURPOSE IN MAIN PROJECT

The AI-SERVICE acts as a separate microservice that:

- Verifies student identity using face recognition
- Helps prevent proxy attendance
- Integrates with the Android attendance application
- Returns a simple verification result to the main backend

------------------------------------------------------------

TECHNOLOGIES USED

- Python 3.10.0
- FastAPI
- Uvicorn
- face_recognition
- NumPy

------------------------------------------------------------

API ENDPOINT

POST /verify

Description:
Compares two face images and checks whether they belong to the same
person.

Request Type:
multipart/form-data

Parameters:
- registered : Registered student face image
- live       : Live captured face image

Sample Request (curl):

curl -X POST http://127.0.0.1:8000/verify \
  -F "registered=@registered.jpg" \
  -F "live=@live.jpg"

Sample Response:

{
  "match": true
}

If face detection fails:

{
  "match": false,
  "reason": "Face not detected in one or both images"
}

------------------------------------------------------------

SETUP INSTRUCTIONS


1. Create Virtual Environment

python -m venv venv


2. Activate the script 

.\venv\Scripts\Activate.ps1


3. Install required packages:

python -m pip install -r requirements.txt


4. Start the service:

uvicorn main:app --reload

Service will run at:
http://127.0.0.1:8000

------------------------------------------------------------

PROJECT STRUCTURE

AI-SERVICE/
|
|-- main.py
|-- requirements.txt
|-- README.txt

------------------------------------------------------------

IMPLEMENTATION NOTES

- Only the first detected face in each image is used
- Face matching tolerance is set to 0.45
- Images are processed in memory and are not stored
- Designed for academic and internal project usage

------------------------------------------------------------

INTEGRATION

This service is called by the Smart Attendance System backend during
attendance marking to confirm the student’s identity before saving
attendance records.

------------------------------------------------------------

AUTHOR

Raunak Ranjan
B.Tech – Computer Science & Engineering
