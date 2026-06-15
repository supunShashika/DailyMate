package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    Button btnEditDetails, btnChangePassword, btnLogout;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings); // Highlight Account

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_completed) {
                startActivity(new Intent(getApplicationContext(), CompletedTaskActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true; // Already here
            }
            return false;
        });

        try {
            btnEditDetails = findViewById(R.id.btnEditDetails);
            btnChangePassword = findViewById(R.id.btnChangePassword);
            btnLogout = findViewById(R.id.btnLogout);
            backButton = findViewById(R.id.backButton);

            if (btnEditDetails == null || btnChangePassword == null || btnLogout == null || backButton == null) {
                Log.e(TAG, "One or more UI elements not found");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            btnEditDetails.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AccountActivity.this, ChangeAccountActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening ChangeAccountActivity: " + e.getMessage(), e);
                    Toast.makeText(AccountActivity.this, "Error opening account settings", Toast.LENGTH_SHORT).show();
                }
            });

            btnChangePassword.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(AccountActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error opening ForgotPasswordActivity: " + e.getMessage(), e);
                    Toast.makeText(AccountActivity.this, "Error opening password settings", Toast.LENGTH_SHORT).show();
                }
            });

            btnLogout.setOnClickListener(v -> {
                try {
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("userId");
                    editor.remove("email");
                    editor.apply();
                    Log.d(TAG, "User logged out");

                    Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error logging out: " + e.getMessage(), e);
                    Toast.makeText(AccountActivity.this, "Error logging out", Toast.LENGTH_SHORT).show();
                }
            });

            backButton.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
