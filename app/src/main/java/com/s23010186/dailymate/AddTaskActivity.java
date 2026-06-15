package com.s23010186.dailymate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    EditText titleInput, descInput, deadlineInput;
    Button saveTaskBtn;
    private double currentLat = 0.0;
    private double currentLng = 0.0;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try {
            // Initialize Database
            dbHelper = new DatabaseHelper(this);

            // Link UI elements (Ensure IDs match your XML layout)
            titleInput = findViewById(R.id.taskTitleEditText);
            descInput = findViewById(R.id.taskDescEditText);
            deadlineInput = findViewById(R.id.taskDeadlineEditText);
            saveTaskBtn = findViewById(R.id.saveTaskButton);
            Button btnGetLocation = findViewById(R.id.btnGetLocation);

            btnGetLocation.setOnClickListener(v -> {
                // For a full app, request Manifest.permission.ACCESS_FINE_LOCATION first
                // If you are using MapActivity, you would launch it here via startActivityForResult
                // For now, we simulate capturing a location (e.g., Colombo, Sri Lanka)
                currentLat = 6.9271;
                currentLng = 79.8612;
                Toast.makeText(this, "Location Captured!", Toast.LENGTH_SHORT).show();
            });


            if (titleInput == null || descInput == null || deadlineInput == null || saveTaskBtn == null) {
                Log.e(TAG, "One or more UI elements not found");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            saveTaskBtn.setOnClickListener(v -> {
                try {
                    String title = titleInput.getText().toString().trim();
                    String desc = descInput.getText().toString().trim();
                    String deadline = deadlineInput.getText().toString().trim();

                    Log.d(TAG, "Save button clicked - Title: " + title + ", Deadline: " + deadline);

                    // Validation
                    if(title.isEmpty()) {
                        Toast.makeText(AddTaskActivity.this, "Please enter a task title", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(deadline.isEmpty()) {
                        Toast.makeText(AddTaskActivity.this, "Please enter a deadline", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get logged-in user ID from SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    int userId = prefs.getInt("userId", -1);

                    Log.d(TAG, "Retrieved userId for task: " + userId);

                    if(userId == -1) {
                        Log.e(TAG, "No user ID in SharedPreferences");
                        Toast.makeText(AddTaskActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Insert into Database
                    Log.d(TAG, "Attempting to insert task with userId: " + userId);
                    boolean isInserted = dbHelper.insertTask(title, desc, deadline, currentLat, currentLng, userId);

                    if(isInserted) {
                        Log.d(TAG, "Task inserted successfully");
                        Toast.makeText(AddTaskActivity.this, "Task Saved!", Toast.LENGTH_SHORT).show();
                        finish(); // Closes screen and returns to Home
                    } else {
                        Log.e(TAG, "Failed to insert task");
                        Toast.makeText(AddTaskActivity.this, "Error: Failed to save task. Check logs for details.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving task: " + e.getMessage(), e);
                    e.printStackTrace();
                    Toast.makeText(AddTaskActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}