package com.s23010186.dailymate;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnGetStarted;
    TextView textSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetStarted = findViewById(R.id.btnGetStarted);
        textSignIn = findViewById(R.id.textSignIn);

        btnGetStarted.setOnClickListener(v -> {
            // Navigate to Sign Up Activity (you can create this next)
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        textSignIn.setOnClickListener(v -> {
            // Navigate to Login Activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}