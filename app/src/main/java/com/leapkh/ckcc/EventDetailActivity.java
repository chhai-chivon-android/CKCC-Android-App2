package com.leapkh.ckcc;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import kh.edu.rupp.ckcc.view.LikeAndShareButton;
import kh.edu.rupp.ckcc.view.OnLikeAndShareButtonClickListener;

public class EventDetailActivity extends AppCompatActivity implements OnLikeAndShareButtonClickListener {

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

        LikeAndShareButton likeAndShareButton = findViewById(R.id.like_and_share_button);
        likeAndShareButton.setViewCount(111, false);

        likeAndShareButton.setOnLikeAndShareButtonClickListener(this);


        likeAndShareButton.setOnLikeAndShareButtonClickListener(new OnLikeAndShareButtonClickListener() {
            @Override
            public void onShareButtonClick() {

            }

            @Override
            public void onLikeButtonClick() {

            }
        });
    }

    @Override
    public void onShareButtonClick() {
        Toast.makeText(this, "onShareButtonClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeButtonClick() {
        Toast.makeText(this, "onLikeButtonClick", Toast.LENGTH_SHORT).show();
    }
}
