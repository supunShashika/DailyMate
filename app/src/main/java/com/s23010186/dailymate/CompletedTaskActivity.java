package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CompletedTaskActivity extends AppCompatActivity {

    ImageView backToHome, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);

        backToHome = findViewById(R.id.backButton);


        // Navigate back to Home
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CompletedTaskActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}