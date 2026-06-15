package com.s23010186.dailymate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private static final String TAG = "TaskAdapter";
    private final Context context;
    private final ArrayList<TaskModel> taskList;
    private final OnTaskClickListener listener;

    // Interface to pass click events to HomeActivity
    public interface OnTaskClickListener {
        void onTaskComplete(TaskModel task, int position);
        void onTaskDelete(TaskModel task, int position);
        void onTaskClick(TaskModel task);
    }

    public TaskAdapter(Context context, ArrayList<TaskModel> taskList, OnTaskClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
            return new TaskViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        try {
            TaskModel task = taskList.get(position);
            holder.tvTaskTitle.setText(task.getTitle());
            holder.tvTaskDeadline.setText(task.getDeadline());

            // Reset listener to prevent unwanted triggers during recycling
            holder.checkboxTask.setOnCheckedChangeListener(null);
            holder.checkboxTask.setChecked(false); // Default to unchecked for pending tasks

            // Handle Checkbox click (Complete Task)
            holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                try {
                    if(isChecked) {
                        listener.onTaskComplete(task, holder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling task complete: " + e.getMessage(), e);
                }
            });

            // Handle Long Click (Delete Task)
            holder.itemView.setOnLongClickListener(v -> {
                try {
                    listener.onTaskDelete(task, holder.getAdapterPosition());
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error handling task delete: " + e.getMessage(), e);
                    return false;
                }
            });

            // Handle normal click to view details
            holder.itemView.setOnClickListener(v -> {
                try {
                    listener.onTaskClick(task);
                } catch (Exception e) {
                    Log.e(TAG, "Error handling task click: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage(), e);
        }
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTitle, tvTaskDeadline;
        CheckBox checkboxTask;

        public TaskViewHolder(View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
            checkboxTask = itemView.findViewById(R.id.checkboxTask);
        }
    }


}