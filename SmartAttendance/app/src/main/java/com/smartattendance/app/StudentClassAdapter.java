package com.smartattendance.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ClassSessionModel;

import java.util.List;

public class StudentClassAdapter
        extends RecyclerView.Adapter<StudentClassAdapter.ViewHolder> {

    private final List<ClassSessionModel> classes;
    private final Context context;

    public StudentClassAdapter(List<ClassSessionModel> classes, Context context) {
        this.classes = classes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_class, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        ClassSessionModel c = classes.get(position);

        holder.subjectText.setText(c.getSubjectName());
        holder.teacherText.setText("Teacher: " + c.getTeacherName());

        // ---------- CLICK → ATTENDANCE ----------
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, AttendanceActivity.class);
            i.putExtra("classId", c.getId()); // ✅ ONLY REQUIRED FIELD
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectText, teacherText;

        ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            teacherText = itemView.findViewById(R.id.teacherText);
        }
    }
}
