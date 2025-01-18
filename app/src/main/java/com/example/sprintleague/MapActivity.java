package com.example.sprintleague;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    private RelativeLayout save, current;
    private ImageView cancel;

    private GoogleMap mMap;
    private Marker redMarker; // To manage the red marker
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private LatLng currentLatLng;
    private String selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        save = findViewById(R.id.map_save);
        current = findViewById(R.id.map_current);
        cancel = findViewById(R.id.map_cancel);


        // Initialize the Fused Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (redMarker != null) {
                    LatLng selectedLatLng = redMarker.getPosition();
                    String selectedAddress = getAddressFromLatLng(selectedLatLng);

                    if (selectedAddress != null) {
                        Intent resultIntent = new Intent();
                        // Send address
                        resultIntent.putExtra("address", selectedAddress);
                        // Send LatLng coordinates
                        resultIntent.putExtra("latitude", selectedLatLng.latitude);
                        resultIntent.putExtra("longitude", selectedLatLng.longitude);
                        // Send formatted Google Maps URL
                        String googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" +
                                selectedLatLng.latitude + "," + selectedLatLng.longitude;
                        resultIntent.putExtra("google_maps_url", googleMapsUrl);

                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(MapActivity.this, "Please select a location.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MapActivity.this, "No location selected.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                moveToCurrentLocation();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        applyMapStyle();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        // Handle map clicks to move the red marker
        mMap.setOnMapClickListener(latLng -> {
            if (redMarker != null) {
                redMarker.remove();
            }
            redMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("Selected Location"));

            // Save the selected location and address
            selectedAddress = getAddressFromLatLng(latLng);
            if (selectedAddress != null) {
//                Toast.makeText(this, selectedAddress, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Address not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Move camera to current location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // Place a red marker at the user's current location
                addMarkerAtLocation(currentLatLng);

                // Save the current location's address
                selectedAddress = getAddressFromLatLng(currentLatLng);
            } else {
                Toast.makeText(this, "Unable to fetch current location.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void moveToCurrentLocation() {
        if (currentLatLng != null) {
            // Move camera to current location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

            // Place a red marker at the user's current location
            addMarkerAtLocation(currentLatLng);

            // Save the current location's address
            selectedAddress = getAddressFromLatLng(currentLatLng);

            if (selectedAddress != null) {
                Toast.makeText(this, selectedAddress, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Current location is unavailable.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarkerAtLocation(LatLng location) {
        if (redMarker != null) {
            redMarker.remove();
        }
        redMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Your Location"));
    }


    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    setupMap();
                }
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void applyMapStyle() {
        try {
            boolean isNightMode = (getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES;

            int styleRes = isNightMode ? R.raw.map_style_dark : R.raw.map_style_light;
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleRes));

            if (!success) {
                Toast.makeText(this, "Failed to apply map style", Toast.LENGTH_SHORT).show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
