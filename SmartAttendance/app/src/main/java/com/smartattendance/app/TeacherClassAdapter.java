package com.smartattendance.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ApiService;
import com.smartattendance.app.network.ClassSessionModel;
import com.smartattendance.app.network.RetrofitClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherClassAdapter
        extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {

    private final List<ClassSessionModel> classes;
    private final String token;
    private final ApiService apiService;

    public TeacherClassAdapter(List<ClassSessionModel> classes, String token) {
        this.classes = classes;
        this.token = token;
        this.apiService = RetrofitClient.getApiService(null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_class, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        ClassSessionModel c = classes.get(position);

        // ---------- SUBJECT ----------
        holder.subjectText.setText(c.getSubjectName());

        // ---------- TIME ----------
        String start = c.getStartTime(); // e.g. 2025-01-10T10:00:00
        String end = c.getEndTime();     // e.g. 2025-01-10T11:00:00

        String date = start.split("T")[0];
        String startTime = start.split("T")[1].substring(0, 5);
        String endTime = end.split("T")[1].substring(0, 5);

        holder.timeText.setText(
                "From " + startTime + " to " + endTime + "\n" +
                        "Date: " + date
        );


        // ---------- ITEM CLICK ----------
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent i = new Intent(context, CreateClassActivity.class);
            i.putExtra("classId", c.getId());
            context.startActivity(i);
        });

        // ---------- DELETE ----------
        holder.btnDelete.setOnClickListener(v -> {

            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Context context = v.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Delete Class")
                    .setMessage("Are you sure you want to delete this class?")
                    .setPositiveButton("Delete", (d, w) -> {

                        apiService.deleteClass(
                                        classes.get(pos).getId(),
                                        "Bearer " + token
                                )
                                .enqueue(new Callback<Map<String, String>>() {

                                    @Override
                                    public void onResponse(
                                            Call<Map<String, String>> call,
                                            Response<Map<String, String>> response) {

                                        if (response.isSuccessful()) {
                                            classes.remove(pos);
                                            notifyItemRemoved(pos);

                                            Toast.makeText(
                                                    context,
                                                    "Class deleted",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        } else {
                                            Toast.makeText(
                                                    context,
                                                    "Delete failed",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<Map<String, String>> call,
                                            Throwable t) {

                                        Toast.makeText(
                                                context,
                                                "Network error",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    // ===============================
    // VIEW HOLDER
    // ===============================
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectText, timeText;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            timeText = itemView.findViewById(R.id.timeText);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
