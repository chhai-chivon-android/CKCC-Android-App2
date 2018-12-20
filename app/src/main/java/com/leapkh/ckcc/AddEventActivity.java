package com.leapkh.ckcc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kh.edu.rupp.ckcc.component.BaseActivity;
import kh.edu.rupp.ckcc.utility.Utility;
import kh.edu.rupp.ckcc.view.ImagesControllerView;
import kh.edu.rupp.ckcc.view.OnImagesControllerViewClickListener;


public class AddEventActivity extends BaseActivity {

    private final int GALLERY_REQUEST_CODE = 1;
    private final int MAP_REQUEST_CODE = 2;


    private ImageView imgEvent;
    private ProgressBar progressBar;
    private Bitmap selectedImage;
    private EditText etxtAddress;

    private ImagesControllerView imagesControllerView;

    private double selectedLat;
    private double selectedLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);
        imgEvent = findViewById(R.id.img_event);
        progressBar = findViewById(R.id.progressBar);
        etxtAddress = findViewById(R.id.etxt_address);

        imagesControllerView = findViewById(R.id.imgControllerView);
        imagesControllerView.setOnImagesControllerViewClickListener(new OnImagesControllerViewClickListener() {
            @Override
            public void onInsertPhotoClick() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });
    }


    public void onPickOnMapClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, MAP_REQUEST_CODE);
    }

    public void onSearchByNameClick(View view){

    }


    public void onSaveButtonClick(View view) {
        // Check if there's internet connection
        if(!isInternetAvailable()){
            showNoInternetConnectionDialog();
            return;
        }

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

        final String title = etxtTitle.getText().toString();
        final String date = etxtDate.getText().toString();

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

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                imagesControllerView.addImage(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == MAP_REQUEST_CODE) {
            double lat = data.getDoubleExtra("lat", 0);
            double lng = data.getDoubleExtra("lng", 0);
            selectedLat = lat;
            selectedLng = lng;
            reverseGeocode(lat, lng);
        }

    }


    private void reverseGeocode(final double lat, final double lng) {
        Log.d("ckcc", "reverseGeocode");
        final Geocoder geocoder = new Geocoder(this, new Locale("km"));
        Thread geocodeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                    final Address address = addresses.get(0);
                    //etxtAddress.setText(address.getAddressLine(0));
                    Log.d("ckcc", "Address: " + address.toString());
                    // Dispatch background thread to main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etxtAddress.setText(address.getAddressLine(0));
                        }
                    });
                } catch (IOException e) {
                    Toast.makeText(AddEventActivity.this, "Error while trying to reverse geocode.", Toast.LENGTH_SHORT).show();
                    Log.d("ckcc", "reverseGeocode error: " + e.getMessage());
                }
            }
        });
        geocodeThread.start();
    }

}


















