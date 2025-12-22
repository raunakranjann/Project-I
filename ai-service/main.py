from fastapi import FastAPI, File, UploadFile, HTTPException
from fastapi.responses import JSONResponse
import face_recognition

app = FastAPI(title="AI Face Verification Service")

# ===============================
# HEALTH CHECK (VERY IMPORTANT)
# ===============================
@app.get("/health")
async def health_check():
    return {
        "status": "UP",
        "service": "AI Face Verification",
    }


# ===============================
# FACE VERIFICATION
# ===============================
@app.post("/verify")
async def verify_faces(
    registered: UploadFile = File(...),
    live: UploadFile = File(...)
):
    try:
        registered_image = face_recognition.load_image_file(registered.file)
        live_image = face_recognition.load_image_file(live.file)

        registered_encodings = face_recognition.face_encodings(registered_image)
        live_encodings = face_recognition.face_encodings(live_image)

        if not registered_encodings or not live_encodings:
            return JSONResponse(
                status_code=200,
                content={
                    "match": False,
                    "reason": "Face not detected in one or both images"
                }
            )

        result = face_recognition.compare_faces(
            [registered_encodings[0]],
            live_encodings[0],
            tolerance=0.45
        )

        return {
            "match": bool(result[0])
        }

    except Exception as e:
        # Any unexpected AI failure
        return JSONResponse(
            status_code=500,
            content={
                "match": False,
                "reason": "AI processing error",
                "detail": str(e)
            }
        )
