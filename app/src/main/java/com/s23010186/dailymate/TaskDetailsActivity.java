package com.s23010186.dailymate;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDesc, tvDeadline, tvStatus;
    Button btnBack;
    DatabaseHelper dbHelper;

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

            // Populate the UI
            tvTitle.setText(title);
            tvDesc.setText(desc != null && !desc.isEmpty() ? desc : "No description provided.");
            tvDeadline.setText("Deadline: " + deadline);
            tvStatus.setText("Status: " + (status == 1 ? "Completed" : "Pending"));

            cursor.close();
        }
    }
}