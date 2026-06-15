package com.s23010186.dailymate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation = null;
    private Button btnConfirmLocation;
    private String mode; // "PICK" or "VIEW"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);
        mode = getIntent().getStringExtra("MODE");

        // Initialize Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle Confirm Button Click (Only for PICK mode)
        if (btnConfirmLocation != null) {
            btnConfirmLocation.setOnClickListener(v -> {
                if (selectedLocation != null) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("LAT", selectedLocation.latitude);
                    resultIntent.putExtra("LNG", selectedLocation.longitude);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(MapActivity.this, "Please tap the map to select a location", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if ("VIEW".equals(mode)) {
            // VIEW MODE: Show the saved location
            double lat = getIntent().getDoubleExtra("LAT", 0.0);
            double lng = getIntent().getDoubleExtra("LNG", 0.0);
            LatLng taskLocation = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(taskLocation).title("Task Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(taskLocation, 15f));
            if (btnConfirmLocation != null) {
                btnConfirmLocation.setVisibility(View.GONE); // Hide confirm button
            }

        } else {
            // PICK MODE: Let user tap to select a location
            if (btnConfirmLocation != null) {
                btnConfirmLocation.setVisibility(View.VISIBLE);
            }

            // Set a default starting location (e.g., Sri Lanka)
            LatLng defaultStart = new LatLng(7.8731, 80.7718);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultStart, 7f));

            mMap.setOnMapClickListener(latLng -> {
                mMap.clear(); // Remove previous markers
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                selectedLocation = latLng;
            });
        }
    }
}