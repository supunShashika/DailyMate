package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeAccountActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        EditText nameEditText = findViewById(R.id.nameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        ImageView profileImage = findViewById(R.id.profileImage);

        // Navigate back to Account page
        backButton.setOnClickListener(v -> finish());

        // Save changes and navigate to Home page
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            Intent intent = new Intent(ChangeAccountActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        });

    }
}

