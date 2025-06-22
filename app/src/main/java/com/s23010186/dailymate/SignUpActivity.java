package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText emailEditText, passwordEditText, nameEditText, usernameEditText;
    private Button signUpButton;
    private TextView backToLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        signUpButton = findViewById(R.id.signUpButton);
        backToLoginText = findViewById(R.id.backToLoginText);

        db = new DatabaseHelper(this);

        signUpButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = db.addUser(name, username, email, password);
            if (result > 0) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });

        backToLoginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}