package com.leapkh.ckcc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private MapView mapView;

    private GoogleMap googleMap;

    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.map_view);

        // Forward container’s life cycle methods to MapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Forward container’s life cycle methods to MapView
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Forward container’s life cycle methods to MapView
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Forward container’s life cycle methods to MapView
        mapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready.", Toast.LENGTH_SHORT).show();

        this.googleMap = googleMap;

        // Add long press listener to Map object
        googleMap.setOnMapLongClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);


        // Load user's location to zoom map to
        loadUserCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadUserCurrentLocation();
            } else {
                Toast.makeText(this, "This app cannot work properly without location permission. You should allow it.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadUserCurrentLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> locationTask = locationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null) {
                        Log.d("ckcc", "Found last known location");
                        // Move camera
                        moveCamera(location);
                    } else {
                        Toast.makeText(MapActivity.this, "Load last known location not found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MapActivity.this, "Load last known location fail.", Toast.LENGTH_LONG).show();
                    Log.d("ckcc", "Load last known location fail: " + task.getException());
                }
                requestLocationUpdate();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        Log.d("ckcc", "requestLocationUpdate");
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("ckcc", "Location updated.");
                Toast.makeText(MapActivity.this, "Location updated.", Toast.LENGTH_LONG).show();
                Location updatedLocation = locationResult.getLastLocation();
                Log.d("ckcc", updatedLocation.getLatitude() + ", " + updatedLocation.getLongitude());
                // Move camera
                moveCamera(updatedLocation);
                locationProviderClient.removeLocationUpdates(this);
            }
        };

        locationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }

    private void moveCamera(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Intent intent = new Intent();
        intent.putExtra("lat", latLng.latitude);
        intent.putExtra("lng", latLng.longitude);
        setResult(RESULT_OK, intent);
        finish();
    }
}



















