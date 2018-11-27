package com.leapkh.ckcc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AddEventActivity extends AppCompatActivity {

    private final int GALLERY_REQUEST_CODE = 1;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private ImageView imgEvent;
    private ProgressBar progressBar;
    private Bitmap selectedImage;
    private EditText etxtCoornidate;

    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);
        imgEvent = findViewById(R.id.img_event);
        progressBar = findViewById(R.id.progressBar);
        etxtCoornidate = findViewById(R.id.etxt_coordinate);

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
                        etxtCoornidate.setText(location.getLatitude() + ", " + location.getLongitude());
                    } else {
                        Toast.makeText(AddEventActivity.this, "Load last known location not found.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddEventActivity.this, "Load last known location fail.", Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddEventActivity.this, "Location updated.", Toast.LENGTH_LONG).show();
                Location updatedLocation = locationResult.getLastLocation();
                Log.d("ckcc", updatedLocation.getLatitude() + ", " + updatedLocation.getLongitude());
                etxtCoornidate.setText(updatedLocation.getLatitude() + ", " + updatedLocation.getLongitude());
                locationProviderClient.removeLocationUpdates(this);
            }
        };

        locationProviderClient.requestLocationUpdates(request, locationCallback, null);
    }

    public void onSaveButtonClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        // Upload image to Firebase storage
        // Create reference
        String fileLocation = "/images/" + System.currentTimeMillis() + ".png";
        final StorageReference imageRef = FirebaseStorage.getInstance().getReference(fileLocation);

        // Convert bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Start upload
        UploadTask uploadTask = imageRef.putBytes(byteArray);
        Task<Uri> urlTastk = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                String imageUrl = task.getResult().toString();
                addEventToFirestore(imageUrl);
            }
        });


    }

    private void addEventToFirestore(String imageUrl) {
        EditText etxtTitle = findViewById(R.id.etxt_title);
        EditText etxtDate = findViewById(R.id.etxt_date);
        EditText etxtLocation = findViewById(R.id.etxt_location);

        final String title = etxtTitle.getText().toString();
        final String date = etxtDate.getText().toString();
        final String location = etxtLocation.getText().toString();

        // Add event to Web service
        /*
        String url = "http://test.js-cambodia.com/ckcc/add-event.php";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(AddEventActivity.this, "Sucess", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddEventActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("date", date);
                params.put("location", location);
                return params;
            }
        };
        requestQueue.add(request);
        */

        // Add event to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("location", location);
        event.put("imageUrl", imageUrl);

        /*db.collection("events").add(event).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddEventActivity.this, "Add event success", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddEventActivity.this, "Add event fail: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });*/


        db.collection("events").add(event).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(AddEventActivity.this, "Add event success", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddEventActivity.this, "Add event fail: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onEventImageViewClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imgEvent.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


















