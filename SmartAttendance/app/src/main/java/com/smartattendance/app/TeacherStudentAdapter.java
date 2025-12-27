package com.smartattendance.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ApiResponse;
import com.smartattendance.app.network.RetrofitClient;
import com.smartattendance.app.network.StudentModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherStudentAdapter
        extends RecyclerView.Adapter<TeacherStudentAdapter.ViewHolder> {

    private final Context context;
    private final List<StudentModel> students;
    private final long classId;

    public TeacherStudentAdapter(
            Context context,
            List<StudentModel> students,
            long classId
    ) {
        this.context = context;
        this.students = students;
        this.classId = classId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_student, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        StudentModel s = students.get(position);

        holder.name.setText(s.getName());
        holder.roll.setText("Roll: " + s.getRollNo());

        // ===============================
        // 🔑 PERSISTED STATE
        // ===============================
        resetButtons(holder);

        if ("PRESENT".equals(s.getAttendanceStatus())) {
            lockPresent(holder);
        } else if ("ABSENT".equals(s.getAttendanceStatus())) {
            lockAbsent(holder);
        }

        // ===============================
        // PRESENT
        // ===============================
        holder.present.setOnClickListener(v ->
                markAttendance(holder, s, "PRESENT")
        );

        // ===============================
        // ABSENT
        // ===============================
        holder.absent.setOnClickListener(v ->
                markAttendance(holder, s, "ABSENT")
        );
    }

    private void markAttendance(
            ViewHolder holder,
            StudentModel student,
            String status
    ) {
        disableButtons(holder);

        RetrofitClient.getApiService(context)
                .markAttendanceByTeacher(
                        classId,
                        student.getId(),
                        status
                )
                .enqueue(new Callback<ApiResponse<String>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<String>> call,
                            Response<ApiResponse<String>> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            // 🔑 SAVE STATE LOCALLY
                            student.setAttendanceStatus(status);

                            if ("PRESENT".equals(status)) {
                                lockPresent(holder);
                            } else {
                                lockAbsent(holder);
                            }

                            Toast.makeText(
                                    context,
                                    response.body().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {
                            Toast.makeText(
                                    context,
                                    "Failed to mark attendance",
                                    Toast.LENGTH_SHORT
                            ).show();
                            resetButtons(holder);
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<String>> call,
                            Throwable t) {

                        Toast.makeText(
                                context,
                                "Network error",
                                Toast.LENGTH_SHORT
                        ).show();
                        resetButtons(holder);
                    }
                });
    }

    // ===============================
    // UI HELPERS
    // ===============================
    private void resetButtons(ViewHolder h) {
        h.present.setEnabled(true);
        h.absent.setEnabled(true);
        h.present.setText("Present");
        h.absent.setText("Absent");
    }

    private void disableButtons(ViewHolder h) {
        h.present.setEnabled(false);
        h.absent.setEnabled(false);
    }

    private void lockPresent(ViewHolder h) {
        h.present.setEnabled(false);
        h.absent.setEnabled(false);
        h.present.setText("✓ Present");
    }

    private void lockAbsent(ViewHolder h) {
        h.present.setEnabled(false);
        h.absent.setEnabled(false);
        h.absent.setText("✗ Absent");
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, roll;
        Button present, absent;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.nameText);
            roll = v.findViewById(R.id.rollText);
            present = v.findViewById(R.id.btnPresent);
            absent = v.findViewById(R.id.btnAbsent);
        }
    }
}
