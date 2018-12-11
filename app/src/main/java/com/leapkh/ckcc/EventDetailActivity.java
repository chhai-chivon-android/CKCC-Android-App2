package com.leapkh.ckcc;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        String eventJson = getIntent().getStringExtra("eventJson");
        Gson gson = new Gson();
        Event event = gson.fromJson(eventJson, Event.class);

        SimpleDraweeView imgEvent = findViewById(R.id.img_event);
        imgEvent.setImageURI(event.getImageUrl());

        TextView txtTitle = findViewById(R.id.txt_title);
        txtTitle.setText(event.getTitle());

    }
}
