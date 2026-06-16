package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast; // Make sure to import Toast
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailEditText, newPasswordEditText, currentPasswordEditText;
    Button changePasswordButton;
    TextView backToLoginText;
    DatabaseHelper dbHelper; // Declare the DatabaseHelper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.emailEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        dbHelper = new DatabaseHelper(this); // Initialize it

        changePasswordButton.setOnClickListener(v -> {
            // 1. Get the text from the inputs
            String email = emailEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String currentPassword = currentPasswordEditText.getText().toString().trim();

            // 2. Validate inputs are not empty
            if (email.isEmpty() || newPassword.isEmpty() || currentPassword.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Attempt to update the password in the database
            boolean isUpdated = dbHelper.updatePassword(email, currentPassword, newPassword);

            if (isUpdated) {
                Toast.makeText(ForgotPasswordActivity.this, "Password Changed Successfully!", Toast.LENGTH_LONG).show();

                // 4. Navigate back to login so they can use the new password
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                // Clear the back stack so they can't hit 'back' to get to this screen again
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Show error if email or current password was wrong
                Toast.makeText(ForgotPasswordActivity.this, "Incorrect Email or Current Password", Toast.LENGTH_LONG).show();
            }
        });

        backToLoginText.setOnClickListener(v -> {
            // Navigate to login page
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}