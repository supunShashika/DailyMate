package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    EditText emailInput, passwordInput, nameInput, usernameInput;
    Button signUpButton;
    TextView backToLogin;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        try {
            dbHelper = new DatabaseHelper(this);

            emailInput = findViewById(R.id.signupEmail);
            passwordInput = findViewById(R.id.signupPassword);
            nameInput = findViewById(R.id.signupName);
            usernameInput = findViewById(R.id.signupUsername);
            signUpButton = findViewById(R.id.btnSignUp);
            backToLogin = findViewById(R.id.tvBackToLogin);

            signUpButton.setOnClickListener(v -> {
                try {
                    String email = emailInput.getText().toString().trim().toLowerCase();
                    String pass = passwordInput.getText().toString().trim();
                    String name = nameInput.getText().toString().trim();
                    String user = usernameInput.getText().toString().trim();

                    if (email.isEmpty() || pass.isEmpty() || name.isEmpty() || user.isEmpty()) {
                        Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pass.length() < 6) {
                        Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(SignUpActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean isRegistered = dbHelper.insertUser(email, pass, name, user);
                    if (isRegistered) {
                        Log.d(TAG, "User registered successfully: " + email);
                        Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Log.w(TAG, "Registration failed: user may already exist");
                        Toast.makeText(SignUpActivity.this, "User already exists. Try logging in.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during sign up: " + e.getMessage(), e);
                    Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            backToLogin.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}