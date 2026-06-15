package com.s23010186.dailymate;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangeAccountActivity extends AppCompatActivity {

    private static final String TAG = "ChangeAccountActivity";
    EditText editName, editUsername;
    Button btnSaveChanges;
    DatabaseHelper dbHelper;
    int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        try {
            dbHelper = new DatabaseHelper(this);
            editName = findViewById(R.id.editAccountName);
            editUsername = findViewById(R.id.editAccountUsername);
            btnSaveChanges = findViewById(R.id.btnSaveChanges);

            if (editName == null || editUsername == null || btnSaveChanges == null) {
                Log.e(TAG, "One or more UI elements not found");
                Toast.makeText(this, "Layout error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            currentUserId = prefs.getInt("userId", -1);

            if (currentUserId != -1) {
                loadUserData();
            } else {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            btnSaveChanges.setOnClickListener(v -> {
                try {
                    String newName = editName.getText().toString().trim();
                    String newUsername = editUsername.getText().toString().trim();

                    if (newName.isEmpty() || newUsername.isEmpty()) {
                        Toast.makeText(ChangeAccountActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = dbHelper.updateUserDetails(currentUserId, newName, newUsername);
                    if (updated) {
                        Log.d(TAG, "Account updated successfully");
                        Toast.makeText(ChangeAccountActivity.this, "Account Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e(TAG, "Failed to update account");
                        Toast.makeText(ChangeAccountActivity.this, "Failed to update account", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating account: " + e.getMessage(), e);
                    Toast.makeText(ChangeAccountActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "Initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails(currentUserId);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));

                editName.setText(name);
                editUsername.setText(username);
                Log.d(TAG, "User data loaded successfully");
            } else {
                Log.e(TAG, "Failed to load user data");
                Toast.makeText(this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user data: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}