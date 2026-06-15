package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText, newPasswordEditText, currentPasswordEditText;
    Button changePasswordButton;
    TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // You can add password change logic here
                // For now, navigate to home
                Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Navigate to login page
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}