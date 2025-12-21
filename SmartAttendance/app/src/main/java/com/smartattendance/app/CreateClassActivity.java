package com.smartattendance.app;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.smartattendance.app.network.ApiResponse;
import com.smartattendance.app.network.CreateClassRequest;
import com.smartattendance.app.network.RetrofitClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateClassActivity extends AppCompatActivity {

    private static final int GPS_PERMISSION_CODE = 101;
    private static final String TAG = "CreateClass";

    private EditText subjectInput, radiusInput, latitudeInput, longitudeInput;
    private Button btnStartTime, btnEndTime, btnFetchGps, createBtn;
    private TextView startTimeText, endTimeText;
    private CheckBox useGpsCheck;
    private ProgressBar loader;

    private LocalDateTime startTime, endTime;
    private Double gpsLat = null;
    private Double gpsLng = null;

    private FusedLocationProviderClient locationClient;

    private String authToken; // 🔐 JWT

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        // ---------- SESSION CHECK ----------
        SharedPreferences prefs =
                getSharedPreferences("login_prefs", MODE_PRIVATE);

        authToken = prefs.getString("auth_token", null);
        String role = prefs.getString("role", null);

        if (authToken == null || !"TEACHER".equals(role)) {
            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        // ---------- UI ----------
        subjectInput = findViewById(R.id.subjectInput);
        radiusInput = findViewById(R.id.radiusInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);

        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        startTimeText = findViewById(R.id.startTimeText);
        endTimeText = findViewById(R.id.endTimeText);

        useGpsCheck = findViewById(R.id.useGpsCheck);
        btnFetchGps = findViewById(R.id.btnFetchGps);

        createBtn = findViewById(R.id.createBtn);
        loader = findViewById(R.id.loader);

        loader.setVisibility(View.GONE);
        btnFetchGps.setEnabled(false);

        locationClient = LocationServices.getFusedLocationProviderClient(this);

        btnStartTime.setOnClickListener(v -> pickDateTime(true));
        btnEndTime.setOnClickListener(v -> pickDateTime(false));

        useGpsCheck.setOnCheckedChangeListener((b, checked) -> {
            btnFetchGps.setEnabled(checked);
            latitudeInput.setEnabled(!checked);
            longitudeInput.setEnabled(!checked);

            if (!checked) {
                gpsLat = null;
                gpsLng = null;
            }
        });

        btnFetchGps.setOnClickListener(v -> fetchGps());
        createBtn.setOnClickListener(v -> createClass());
    }

    // -----------------------------------------------------

    private void pickDateTime(boolean isStart) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this,
                (d, y, m, day) ->
                        new TimePickerDialog(this,
                                (t, h, min) -> {

                                    LocalDateTime dt =
                                            LocalDateTime.of(y, m + 1, day, h, min);

                                    if (isStart) {
                                        startTime = dt;
                                        startTimeText.setText(dt.format(formatter));
                                    } else {
                                        endTime = dt;
                                        endTimeText.setText(dt.format(formatter));
                                    }
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                true).show(),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // -----------------------------------------------------

    private void fetchGps() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_PERMISSION_CODE
            );
            return;
        }

        locationClient.getLastLocation()
                .addOnSuccessListener(this::handleLocation)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Unable to fetch GPS location",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void handleLocation(Location loc) {
        if (loc == null) {
            Toast.makeText(
                    this,
                    "GPS unavailable. Turn on location.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        gpsLat = loc.getLatitude();
        gpsLng = loc.getLongitude();

        latitudeInput.setText(String.valueOf(gpsLat));
        longitudeInput.setText(String.valueOf(gpsLng));

        Toast.makeText(
                this,
                "Location fetched successfully",
                Toast.LENGTH_SHORT
        ).show();
    }

    // -----------------------------------------------------

    private void createClass() {

        if (startTime == null || endTime == null) {
            Toast.makeText(
                    this,
                    "Select start and end time",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String subject = subjectInput.getText().toString().trim();
        String radiusStr = radiusInput.getText().toString().trim();

        if (subject.isEmpty() || radiusStr.isEmpty()) {
            Toast.makeText(
                    this,
                    "Subject and radius required",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        double lat, lng, radius;

        try {
            radius = Double.parseDouble(radiusStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid radius", Toast.LENGTH_SHORT).show();
            return;
        }

        if (useGpsCheck.isChecked()) {
            if (gpsLat == null || gpsLng == null) {
                Toast.makeText(this, "Fetch GPS first", Toast.LENGTH_SHORT).show();
                return;
            }
            lat = gpsLat;
            lng = gpsLng;
        } else {
            try {
                lat = Double.parseDouble(latitudeInput.getText().toString().trim());
                lng = Double.parseDouble(longitudeInput.getText().toString().trim());
            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Invalid latitude/longitude",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
        }

        CreateClassRequest req = new CreateClassRequest(
                subject,
                lat,
                lng,
                radius,
                startTime.format(formatter),
                endTime.format(formatter)
        );

        Log.d(TAG, "Creating class -> " + req.getSubjectName());

        loader.setVisibility(View.VISIBLE);
        createBtn.setEnabled(false);

        RetrofitClient.getApiService(this)
                .createClass(req, "Bearer " + authToken)
                .enqueue(new Callback<ApiResponse>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse> call,
                            Response<ApiResponse> response) {

                        loader.setVisibility(View.GONE);
                        createBtn.setEnabled(true);

                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    CreateClassActivity.this,
                                    "Server error: " + response.code(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        Toast.makeText(
                                CreateClassActivity.this,
                                "Class created successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        loader.setVisibility(View.GONE);
                        createBtn.setEnabled(true);

                        Toast.makeText(
                                CreateClassActivity.this,
                                "Network error",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    // -----------------------------------------------------

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == GPS_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            fetchGps();
        }
    }
}
