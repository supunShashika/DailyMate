package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    Button btnEditDetails, btnChangePassword, btnLogout;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        btnEditDetails = findViewById(R.id.btnEditDetails);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);
        backButton = findViewById(R.id.backButton);

        btnEditDetails.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ChangeAccountActivity.class);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        backButton.setOnClickListener(v -> {
            finish(); // Or navigate to login
        });
    }
}
