package com.s23010186.dailymate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";
    TextView tvName, tvUsername;
    DatabaseHelper dbHelper;
    int currentUserId = -1;
    Button btnEditDetails, btnChangePassword, btnLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_settings); // Highlight Account

        dbHelper = new DatabaseHelper(this);
        tvName = findViewById(R.id.tvAccountName);
        tvUsername = findViewById(R.id.tvAccountUsername);

        // --- NEW: Fetch the logged-in User ID ---
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

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

            if (btnEditDetails == null || btnChangePassword == null || btnLogout == null) {
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
                    // SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE); // Already declared above
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

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ==========================================
    // ADDED METHODS FOR LOADING USER PROFILE
    // ==========================================

    @Override
    protected void onResume() {
        super.onResume();
        // Automatically fetch and update the name every time this screen becomes visible
        if (currentUserId != -1) {
            loadUserProfile(currentUserId);
        }
    }

    private void loadUserProfile(int userId) {
        Cursor cursor = dbHelper.getUserDetails(userId);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

            // Update the TextViews
            tvName.setText(name);
            tvUsername.setText("@" + username);

            cursor.close();
        }
    }
}