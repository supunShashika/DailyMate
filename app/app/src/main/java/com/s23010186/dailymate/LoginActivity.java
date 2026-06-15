package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    EditText emailInput, passwordInput;
    Button loginButton;
    TextView goToSignUp;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            dbHelper = new DatabaseHelper(this);

            emailInput = findViewById(R.id.loginEmail);
            passwordInput = findViewById(R.id.loginPassword);
            loginButton = findViewById(R.id.btnLogin);
            goToSignUp = findViewById(R.id.tvGoToSignUp);

            loginButton.setOnClickListener(v -> {
                String email = emailInput.getText().toString().trim().toLowerCase();
                String pass = passwordInput.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    if (dbHelper.checkUser(email, pass)) {
                        // Get user ID
                        int userId = getUserId(email);
                        
                        if (userId == -1) {
                            Log.e(TAG, "Failed to retrieve user ID");
                            Toast.makeText(LoginActivity.this, "Error retrieving user info", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        Log.d(TAG, "User ID retrieved: " + userId);
                        Log.d(TAG, "Total users in DB: " + dbHelper.getUserCount());
                        
                        // Store user ID in SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("userId", userId);
                        editor.putString("email", email);
                        editor.apply();
                        
                        Log.d(TAG, "User ID stored in SharedPreferences");
                        
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w(TAG, "Invalid credentials for user: " + email);
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during login: " + e.getMessage(), e);
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Login error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // Navigate to Sign Up screen
            goToSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int getUserId(String email) {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query(
                "users", new String[]{"id"}, "email = ?", new String[]{email}, 
                null, null, null);
            int userId = -1;
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
            }
            Log.d(TAG, "getUserId query result: " + userId);
            return userId;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user ID: " + e.getMessage(), e);
            e.printStackTrace();
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}