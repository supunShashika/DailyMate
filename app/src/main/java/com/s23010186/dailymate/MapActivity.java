package com.s23010186.dailymate;

    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.location.Address;
    import android.location.Geocoder;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Toast;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.MarkerOptions;
    import java.io.IOException;
    import java.util.List;
    import java.util.Locale;

    public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

        private GoogleMap myMap;
        private EditText addressInput;
        private Button showLocation;
        private ImageView backButton;
        private String selectedAddress = "";
        private static final float DEFAULT_ZOOM = 15f;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);

            addressInput = findViewById(R.id.editTextAddress);
            showLocation = findViewById(R.id.buttonShowLocation);
            backButton = findViewById(R.id.backToTaskDetails);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            showLocation.setOnClickListener(v -> searchLocation());

            backButton.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selected_location", selectedAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            myMap = googleMap;
            myMap.getUiSettings().setZoomControlsEnabled(true);
            checkLocationPermission();
        }

        private void searchLocation() {
            String location = addressInput.getText().toString().trim();
            if (location.isEmpty()) {
                Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
                return;
            }

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    selectedAddress = address.getAddressLine(0);

                    myMap.clear();
                    myMap.addMarker(new MarkerOptions().position(latLng).title(selectedAddress));
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                    Toast.makeText(this, "Location found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show();
            }
        }

        private void checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                myMap.setMyLocationEnabled(true);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (myMap != null) {
                        checkLocationPermission();
                    }
                }
            }
        }
    }