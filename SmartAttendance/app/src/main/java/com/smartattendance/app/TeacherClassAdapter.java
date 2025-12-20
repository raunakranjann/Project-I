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

public class TeacherClassAdapter
        extends RecyclerView.Adapter<TeacherClassAdapter.ViewHolder> {

    private final List<ClassSessionModel> classes;

    public TeacherClassAdapter(List<ClassSessionModel> classes) {
        this.classes = classes;
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

        holder.subjectText.setText(c.getSubjectName());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();

            Intent i = new Intent(context, CreateClassActivity.class);
            i.putExtra("classId", c.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView subjectText;

        ViewHolder(View itemView) {
            super(itemView);
            subjectText = itemView.findViewById(R.id.subjectText);
        }
    }
}
