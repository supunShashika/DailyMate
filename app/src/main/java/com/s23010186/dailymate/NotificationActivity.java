package com.s23010186.dailymate;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;

public class NotificationActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton deleteButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.deleteButton);

        backButton.setOnClickListener(v -> {
            finish(); // Or navigate to home
        });

        deleteButton.setOnClickListener(v -> {

        });
    }
}