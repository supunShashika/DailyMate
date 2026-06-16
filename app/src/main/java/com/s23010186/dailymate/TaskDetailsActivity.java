package com.s23010186.dailymate;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc, tvDeadline, tvStatus;
    Button btnBack, btnViewMap, btnDeleteTask; // Added btnDeleteTask
    DatabaseHelper dbHelper;
    int currentTaskId = -1; // Store the ID globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        tvTitle = findViewById(R.id.viewTaskTitle);
        tvDesc = findViewById(R.id.viewTaskDesc);
        tvDeadline = findViewById(R.id.viewTaskDeadline);
        tvStatus = findViewById(R.id.viewTaskStatus);
        btnBack = findViewById(R.id.btnBackToList);
        btnViewMap = findViewById(R.id.btnViewLocationMap);
        btnDeleteTask = findViewById(R.id.btnDeleteTask); // Initialize it

        dbHelper = new DatabaseHelper(this);

        // Get the Task ID
        currentTaskId = getIntent().getIntExtra("TASK_ID", -1);

        if (currentTaskId != -1) {
            loadTaskDetails(currentTaskId);
        } else {
            Toast.makeText(this, "Error loading task details", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Handle Delete Button Click
        btnDeleteTask.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteTask(currentTaskId);
            if (isDeleted) {
                Toast.makeText(TaskDetailsActivity.this, "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close this screen and return to the list

                NotificationUtils.sendNotification(this, "Task Deleted", "A task was permanently removed.");
            } else {
                Toast.makeText(TaskDetailsActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTaskDetails(int taskId) {
        Cursor cursor = dbHelper.getTaskById(taskId);

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            tvTitle.setText(title);
            tvDesc.setText(desc != null && !desc.isEmpty() ? desc : "No description provided.");
            tvDeadline.setText("Deadline: " + deadline);
            tvStatus.setText("Status: " + (status == 1 ? "Completed" : "Pending"));

            if (lat != 0.0 && lng != 0.0) {
                btnViewMap.setVisibility(View.VISIBLE);
                btnViewMap.setOnClickListener(v -> {
                    Intent mapIntent = new Intent(TaskDetailsActivity.this, MapActivity.class);
                    mapIntent.putExtra("MODE", "VIEW");
                    mapIntent.putExtra("LAT", lat);
                    mapIntent.putExtra("LNG", lng);
                    startActivity(mapIntent);
                });
            }
            cursor.close();
        }
    }
}