package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    ImageView backToHome, deleteTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        backToHome = findViewById(R.id.backToHome);


        // Go back to Home page
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(TaskDetailsActivity.this, HomeActivity.class);
                startActivity(backIntent);
                finish();
            }
        });


    }
}