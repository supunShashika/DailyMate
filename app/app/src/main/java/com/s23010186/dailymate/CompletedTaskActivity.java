package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CompletedTaskActivity extends AppCompatActivity {

    private static final String TAG = "CompletedTaskActivity";
    DatabaseHelper dbHelper;
    RecyclerView recyclerView;
    ArrayList<TaskModel> completedTaskList;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_completed); // Highlight Completed

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_completed) {
                return true; // Already here
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        try {
            dbHelper = new DatabaseHelper(this);
            recyclerView = findViewById(R.id.completedTasksRecyclerView);
            
            if (recyclerView == null) {
                Log.e(TAG, "RecyclerView not found in layout");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            completedTaskList = new ArrayList<>();

            loadCompletedTasks();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Error initializing activity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCompletedTasks() {
        try {
            completedTaskList.clear();
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            if (userId == -1) {
                Log.w(TAG, "User not logged in");
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.getCompletedTasks(userId);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                    completedTaskList.add(new TaskModel(id, title, deadline));
                }
                cursor.close();
            } else {
                if (cursor != null) {
                    cursor.close();
                }
                Toast.makeText(this, "No completed tasks found", Toast.LENGTH_SHORT).show();
            }

            adapter = new TaskAdapter(this, completedTaskList, new TaskAdapter.OnTaskClickListener() {
                @Override
                public void onTaskComplete(TaskModel task, int position) {
                    // Revert task back to pending (status = 0)
                    boolean updated = dbHelper.updateTaskStatus(task.id, 0);
                    if (updated) {
                        completedTaskList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(CompletedTaskActivity.this, "Task reverted to pending", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTaskDelete(TaskModel task, int position) {
                    boolean deleted = dbHelper.deleteTask(task.id);
                    if (deleted) {
                        completedTaskList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(CompletedTaskActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTaskClick(TaskModel task) {
                    Intent intent = new Intent(CompletedTaskActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("TASK_ID", task.id);
                    startActivity(intent);
                }
            });

            recyclerView.setAdapter(adapter);
            Log.d(TAG, "Completed tasks loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading completed tasks: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}