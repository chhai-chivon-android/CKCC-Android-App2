package com.leapkh.ckcc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LikeAndShareButton extends FrameLayout {

    private TextView txtViewCount;

    public LikeAndShareButton(Context context) {
        super(context);
        initView(context);
    }

    public LikeAndShareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initCustomAttributes(context, attrs);
    }

    public LikeAndShareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initCustomAttributes(context, attrs);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_like_and_share_button, this);
        txtViewCount = findViewById(R.id.txt_view_count);
    }

    private void initCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LikeAndShareButton, 0, 0);
        int viewCount = typedArray.getInt(R.styleable.LikeAndShareButton_view_count, 0);
        txtViewCount.setText("Views: " + viewCount);
        typedArray.recycle();
    }

}
