package com.s23010186.dailymate;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText editSearch;
    private ImageView backToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editSearch = findViewById(R.id.editSearch);
        backToHome = findViewById(R.id.backToHome);

        backToHome.setOnClickListener(v -> finish());

        // Handle search when user presses enter/search on keyboard
        editSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String keyword = editSearch.getText().toString().trim();
        if (!keyword.isEmpty()) {
            Toast.makeText(this, "Searching for: " + keyword, Toast.LENGTH_SHORT).show();
            // TODO: Implement search functionality
        } else {
            Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
        }
    }
}