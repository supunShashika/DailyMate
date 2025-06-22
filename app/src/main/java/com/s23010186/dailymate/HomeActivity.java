package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    ImageButton homeButton, progressButton, completedButton, accountButton, addTaskButton;
    ImageView searchIcon, notificationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        progressButton = findViewById(R.id.progressButton);
        completedButton = findViewById(R.id.completedButton);
        accountButton = findViewById(R.id.accountButton);
        addTaskButton = findViewById(R.id.addTaskButton);
        searchIcon = findViewById(R.id.searchIcon);
        notificationIcon = findViewById(R.id.notificationIcon);


        progressButton.setOnClickListener(v -> startActivity(new Intent(this, ProgressActivity.class)));
        completedButton.setOnClickListener(v -> startActivity(new Intent(this, CompletedTaskActivity.class)));
        accountButton.setOnClickListener(v -> startActivity(new Intent(this, AccountActivity.class)));
        addTaskButton.setOnClickListener(v -> startActivity(new Intent(this, AddTaskActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        notificationIcon.setOnClickListener(v -> startActivity(new Intent(this, NotificationActivity.class)));
    }
}
