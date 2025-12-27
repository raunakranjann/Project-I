package com.smartattendance.app;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import com.smartattendance.app.network.*;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.*;

public class AttendanceActivity extends AppCompatActivity {

    private PreviewView cameraPreview;
    private TextView livenessInstruction, blinkStatus;
    private Button markAttendanceBtn;
    private Switch locationSwitch;
    private EditText latitudeInput, longitudeInput;
    private FrameLayout loaderOverlay;

    private ExecutorService cameraExecutor;
    private FaceDetector faceDetector;

    private int blinkCount = 0;
    private boolean wasEyeOpen = true;
    private boolean livenessVerified = false;
    private Bitmap finalSelfie;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private FusedLocationProviderClient locationClient;

    private long classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_attendance);

        cameraPreview = findViewById(R.id.cameraPreview);
        livenessInstruction = findViewById(R.id.livenessInstruction);
        blinkStatus = findViewById(R.id.blinkStatus);
        markAttendanceBtn = findViewById(R.id.markAttendanceBtn);
        locationSwitch = findViewById(R.id.locationSwitch);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        loaderOverlay = findViewById(R.id.loaderOverlay);

        markAttendanceBtn.setEnabled(false);
        loaderOverlay.setVisibility(View.GONE);

        cameraExecutor = Executors.newSingleThreadExecutor();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        classId = getIntent().getLongExtra("classId", -1);
        if (classId <= 0) {
            Toast.makeText(this, "Invalid class", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔐 JWT SESSION CHECK (FINAL)
        SharedPreferences prefs =
                getSharedPreferences("auth", MODE_PRIVATE);

        String token = prefs.getString("jwt", null);
        String role  = prefs.getString("role", null);

        if (token == null || !"STUDENT".equals(role)) {
            Toast.makeText(this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupFaceDetector();
        requestPermissions();
        setupLocationToggle();

        markAttendanceBtn.setOnClickListener(v -> submitAttendance());
    }

    // ================= FACE DETECTOR =================
    private void setupFaceDetector() {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        faceDetector = FaceDetection.getClient(options);
    }

    // ================= CAMERA =================
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                ImageAnalysis analysis =
                        new ImageAnalysis.Builder()
                                .setTargetResolution(new Size(480, 640))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                analysis.setAnalyzer(cameraExecutor, this::processFrame);

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        analysis
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processFrame(ImageProxy imageProxy) {
        if (livenessVerified) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    if (faces.isEmpty()) return;

                    Face face = faces.get(0);
                    Float left = face.getLeftEyeOpenProbability();
                    Float right = face.getRightEyeOpenProbability();

                    if (left == null || right == null) return;

                    boolean eyesOpen = left > 0.6 && right > 0.6;
                    boolean eyesClosed = left < 0.3 && right < 0.3;

                    if (wasEyeOpen && eyesClosed) wasEyeOpen = false;

                    if (!wasEyeOpen && eyesOpen) {
                        blinkCount++;
                        wasEyeOpen = true;

                        runOnUiThread(() ->
                                blinkStatus.setText(
                                        "Blink count: " + blinkCount + " / 3"
                                ));

                        if (blinkCount >= 3) {
                            livenessVerified = true;
                            captureFinalFrame();
                        }
                    }
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void captureFinalFrame() {
        finalSelfie = cameraPreview.getBitmap();
        runOnUiThread(() -> {
            livenessInstruction.setText("Liveness verified ✓");
            markAttendanceBtn.setEnabled(true);
        });
    }

    // ================= LOCATION =================
    private void setupLocationToggle() {
        locationSwitch.setChecked(true);
        latitudeInput.setEnabled(false);
        longitudeInput.setEnabled(false);

        locationSwitch.setOnCheckedChangeListener((b, checked) -> {
            latitudeInput.setEnabled(!checked);
            longitudeInput.setEnabled(!checked);
            if (checked) fetchGps();
        });

        fetchGps();
    }

    private void fetchGps() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationClient.getLastLocation()
                .addOnSuccessListener(loc -> {
                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                });
    }

    // ================= SUBMIT =================
    private void submitAttendance() {

        if (finalSelfie == null) return;

        if (!locationSwitch.isChecked()) {

            if (latitudeInput.getText().toString().isEmpty()
                    || longitudeInput.getText().toString().isEmpty()) {

                Toast.makeText(this,
                        "Enter latitude and longitude",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                latitude = Double.parseDouble(latitudeInput.getText().toString());
                longitude = Double.parseDouble(longitudeInput.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this,
                        "Invalid coordinates",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        loaderOverlay.setVisibility(View.VISIBLE);
        markAttendanceBtn.setEnabled(false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        finalSelfie.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        MultipartBody.Part selfie =
                MultipartBody.Part.createFormData(
                        "selfie",
                        "selfie.jpg",
                        RequestBody.create(
                                stream.toByteArray(),
                                MediaType.parse("image/jpeg")));

        RetrofitClient.getApiService(this)
                .markAttendance(
                        RequestBody.create(
                                String.valueOf(classId),
                                MediaType.parse("text/plain")),
                        RequestBody.create(
                                String.valueOf(latitude),
                                MediaType.parse("text/plain")),
                        RequestBody.create(
                                String.valueOf(longitude),
                                MediaType.parse("text/plain")),
                        selfie
                )
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse> call,
                            Response<ApiResponse> response) {

                        loaderOverlay.setVisibility(View.GONE);
                        markAttendanceBtn.setEnabled(true);

                        if (response.code() == 401) {
                            Toast.makeText(
                                    AttendanceActivity.this,
                                    "Session expired. Login again.",
                                    Toast.LENGTH_LONG
                            ).show();
                            finish();
                            return;
                        }

                        ApiResponse body = response.body();

                        Toast.makeText(
                                AttendanceActivity.this,
                                body != null
                                        ? body.getMessage()
                                        : "Attendance failed",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        loaderOverlay.setVisibility(View.GONE);
                        markAttendanceBtn.setEnabled(true);

                        Toast.makeText(
                                AttendanceActivity.this,
                                "AI server is down. Please try again later.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                101);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }
}
