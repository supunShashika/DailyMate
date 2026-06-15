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
    Button btnBack;
    DatabaseHelper dbHelper;
    Button btnViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task); // We will create this XML next

        tvTitle = findViewById(R.id.viewTaskTitle);
        tvDesc = findViewById(R.id.viewTaskDesc);
        tvDeadline = findViewById(R.id.viewTaskDeadline);
        tvStatus = findViewById(R.id.viewTaskStatus);
        btnBack = findViewById(R.id.btnBackToList);
        dbHelper = new DatabaseHelper(this);
        btnViewMap = findViewById(R.id.btnViewLocationMap);

        // Get the Task ID passed from the previous screen
        int taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId != -1) {
            loadTaskDetails(taskId);
        } else {
            Toast.makeText(this, "Error loading task details", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTaskDetails(int taskId) {
        Cursor cursor = dbHelper.getTaskById(taskId);

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

            // Retrieve coordinates
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

            // Populate the UI
            tvTitle.setText(title);
            tvDesc.setText(desc != null && !desc.isEmpty() ? desc : "No description provided.");
            tvDeadline.setText("Deadline: " + deadline);
            tvStatus.setText("Status: " + (status == 1 ? "Completed" : "Pending"));

            // Show map button only if coordinates are valid (not 0.0, 0.0)
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