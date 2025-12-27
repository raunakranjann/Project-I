package com.smartattendance.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartattendance.app.network.ClassSessionModel;

import java.util.List;

public class TeacherClassAdapter
        extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {

    // ===============================
    // DATA
    // ===============================
    private final List<ClassSessionModel> classes;
    private final OnClassClickListener listener;

    // ===============================
    // CLICK CALLBACK INTERFACE
    // ===============================
    public interface OnClassClickListener {
        void onClassClick(ClassSessionModel session);
    }

    // ===============================
    // CONSTRUCTOR
    // ===============================
    public TeacherClassAdapter(
            List<ClassSessionModel> classes,
            OnClassClickListener listener
    ) {
        this.classes = classes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_class, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        ClassSessionModel c = classes.get(position);

        holder.subjectText.setText(c.getSubjectName());

        // TIME FORMAT (SAFE)
        String start = c.getStartTime().split("T")[1].substring(0, 5);
        String end   = c.getEndTime().split("T")[1].substring(0, 5);
        String date  = c.getStartTime().split("T")[0];

        holder.teacherText.setText(date + "  " + start + " - " + end);

        // ✅ DELEGATE CLICK
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassClick(c);
            }
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

        TextView subjectText, teacherText;

        ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
            teacherText = itemView.findViewById(R.id.teacherText);
        }
    }
}
