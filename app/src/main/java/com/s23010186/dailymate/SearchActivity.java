package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    EditText editSearchKeyword;
    RecyclerView recyclerView;
    ImageButton btnBack;
    DatabaseHelper dbHelper;
    TaskAdapter adapter;
    ArrayList<TaskModel> searchResults;
    int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editSearchKeyword = findViewById(R.id.editSearchKeyword);
        recyclerView = findViewById(R.id.searchRecyclerView);
        btnBack = findViewById(R.id.btnBackFromSearch);

        dbHelper = new DatabaseHelper(this);
        searchResults = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        // Setup Adapter
        adapter = new TaskAdapter(this, searchResults, new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskComplete(TaskModel task, int position) {
                // Ignore complete action in search view or handle it
            }

            @Override
            public void onTaskDelete(TaskModel task, int position) {
                // Ignore delete action in search view or handle it
            }

            @Override
            public void onTaskClick(TaskModel task) {
                Intent intent = new Intent(SearchActivity.this, TaskDetailsActivity.class);
                intent.putExtra("TASK_ID", task.id);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        // Listen for typing
        editSearchKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void performSearch(String keyword) {
        searchResults.clear();
        if (keyword.isEmpty() || currentUserId == -1) {
            adapter.notifyDataSetChanged();
            return;
        }

        Cursor cursor = dbHelper.searchTasks(currentUserId, keyword);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
                searchResults.add(new TaskModel(id, title, deadline));
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}