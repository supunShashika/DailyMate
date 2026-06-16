package com.s23010186.dailymate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
    private String mode;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);
        mode = getIntent().getStringExtra("MODE");

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnConfirmLocation.setOnClickListener(v -> {
            if (selectedLocation != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("LAT", selectedLocation.latitude);
                resultIntent.putExtra("LNG", selectedLocation.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please tap the map to select a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if ("VIEW".equals(mode)) {
            double lat = getIntent().getDoubleExtra("LAT", 0.0);
            double lng = getIntent().getDoubleExtra("LNG", 0.0);
            LatLng taskLocation = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions().position(taskLocation).title("Task Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(taskLocation, 15f));
            btnConfirmLocation.setVisibility(View.GONE);

        } else {
            // PICK MODE: Setup map for user interaction and get current GPS location
            btnConfirmLocation.setVisibility(View.VISIBLE);

            // Allow manual tapping to change location
            mMap.setOnMapClickListener(latLng -> {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
                selectedLocation = latLng;
            });

            // Trigger the GPS location fetch
            enableUserLocation();
        }
    }

    private void enableUserLocation() {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Enables the native Google Maps "My Location" blue dot and button
            mMap.setMyLocationEnabled(true);

            // Get the last known device location
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Automatically drop the pin on the user's current location
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    selectedLocation = currentLatLng;
                } else {
                    Toast.makeText(this, "Unable to find current location. Please tap the map.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    // Handle the user's response to the permission popup
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch the location now!
                enableUserLocation();
            } else {
                Toast.makeText(this, "GPS permission denied. You can manually pick a location.", Toast.LENGTH_LONG).show();
                // Set a default fallback view if they deny GPS (e.g., center of Sri Lanka)
                LatLng defaultStart = new LatLng(6.882974888580932, 79.88671197669935);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultStart, 7f));
            }
        }
    }
}