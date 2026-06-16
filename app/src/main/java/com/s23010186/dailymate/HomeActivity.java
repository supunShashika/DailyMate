package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    DatabaseHelper dbHelper;
    FloatingActionButton fabAddTask;
    RecyclerView recyclerView;
    ArrayList<TaskModel> taskList;
    TaskAdapter adapter;
    TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Assuming you have an ImageButton with id btnSearch in activity_home.xml
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Highlight Home

        // Create the notification channel
        NotificationUtils.createNotificationChannel(this);

        // Request permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true; // Already here
            } else if (itemId == R.id.nav_completed) {
                startActivity(new Intent(getApplicationContext(), CompletedTaskActivity.class));
                overridePendingTransition(0, 0); // Removes transition animation
                finish(); // Closes current activity to prevent back-button bloat
                return true;
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
            fabAddTask = findViewById(R.id.fab_add_task);
            recyclerView = findViewById(R.id.tasksRecyclerView);

            if (recyclerView == null) {
                Log.e(TAG, "RecyclerView not found in layout");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                return;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            taskList = new ArrayList<>();

            fabAddTask.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
                startActivity(intent);
            });

            loadTasks();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            loadTasks();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }

    private void loadTasks() {
        try {
            taskList.clear();
            
            // Get logged-in user ID from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);
            String email = prefs.getString("email", "Unknown");

            Log.d(TAG, "Retrieved userId: " + userId + ", email: " + email);
            Log.d(TAG, "Total users in DB: " + dbHelper.getUserCount());
            Log.d(TAG, "Total tasks in DB: " + dbHelper.getTaskCount());

            if(userId == -1) {
                Log.w(TAG, "User not logged in, redirecting to login");
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                // Initialize empty adapter to prevent null pointer
                if (adapter == null) {
                    adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnTaskClickListener() {
                        @Override
                        public void onTaskComplete(TaskModel task, int position) {}
                        
                        @Override
                        public void onTaskDelete(TaskModel task, int position) {}
                        
                        @Override
                        public void onTaskClick(TaskModel task) {
                            Intent intent = new Intent(HomeActivity.this, TaskDetailsActivity.class);
                            intent.putExtra("TASK_ID", task.id);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
                return;
            }

            Cursor cursor = dbHelper.getPendingTasks(userId);

            if (cursor == null) {
                Log.e(TAG, "Cursor is null when fetching tasks");
                adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnTaskClickListener() {
                    @Override
                    public void onTaskComplete(TaskModel task, int position) {}
                    
                    @Override
                    public void onTaskDelete(TaskModel task, int position) {}
                    
                    @Override
                    public void onTaskClick(TaskModel task) {
                        Intent intent = new Intent(HomeActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("TASK_ID", task.id);
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
                return;
            }

            Log.d(TAG, "Cursor count for userId " + userId + ": " + cursor.getCount());

            if (cursor.getCount() == 0) {
                Log.d(TAG, "No tasks found for user " + userId);
                Toast.makeText(this, "No pending tasks", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));

                    Log.d(TAG, "Adding task: id=" + id + ", title=" + title + ", deadline=" + deadline);
                    taskList.add(new TaskModel(id, title, deadline));
                }
            }
            cursor.close();

            adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnTaskClickListener() {
                @Override
                public void onTaskComplete(TaskModel task, int position) {
                    boolean updated = dbHelper.updateTaskStatus(task.id, 1);
                    if(updated) {
                        taskList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(HomeActivity.this, "Task Completed!", Toast.LENGTH_SHORT).show();

                        NotificationUtils.sendNotification(HomeActivity.this, "Task Completed!", "Great job finishing a task!");
                    }
                }

                @Override
                public void onTaskDelete(TaskModel task, int position) {
                    boolean deleted = dbHelper.deleteTask(task.id);
                    if(deleted) {
                        taskList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(HomeActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                    }
                }

                // --- ADD THIS NEW OVERRIDE ---
                @Override
                public void onTaskClick(TaskModel task) {
                    // Pass the Task ID to the Details Activity
                    Intent intent = new Intent(HomeActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("TASK_ID", task.id);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            
            Log.d(TAG, "Tasks loaded successfully, count: " + taskList.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading tasks: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            // Still set empty adapter to prevent crashes
            if (recyclerView != null) {
                taskList.clear();
                adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnTaskClickListener() {
                    @Override
                    public void onTaskComplete(TaskModel task, int position) {}
                    
                    @Override
                    public void onTaskDelete(TaskModel task, int position) {}
                    
                    @Override
                    public void onTaskClick(TaskModel task) {
                        Intent intent = new Intent(HomeActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("TASK_ID", task.id);
                        startActivity(intent);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }
    }
}