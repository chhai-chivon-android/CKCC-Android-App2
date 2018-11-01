package com.leapkh.ckcc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddEventActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);
        progressBar = findViewById(R.id.progressBar);
    }

    public void onSaveButtonClick(View view) {
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
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("date", date);
        event.put("location", location);
        event.put("imageUrl", "http://test.js-cambodia.com/ckcc/images/android.png");

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


        db.collection("events").document("2").set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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

}


















