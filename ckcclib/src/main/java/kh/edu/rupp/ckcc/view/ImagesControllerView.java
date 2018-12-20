package kh.edu.rupp.ckcc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vayofm.ckcclib.R;

public class ImagesControllerView extends FrameLayout {

    private OnImagesControllerViewClickListener onImagesControllerViewClickListener;

    public ImagesControllerView(Context context) {
        super(context);

        initView(context);
    }

    public ImagesControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImagesControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setOnImagesControllerViewClickListener(OnImagesControllerViewClickListener onImagesControllerViewClickListener) {
        this.onImagesControllerViewClickListener = onImagesControllerViewClickListener;
    }

    public void addImage(Bitmap bitmap) {
        final LinearLayout lytContainer = findViewById(R.id.lyt_container);
        ViewGroup thumbnailView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_image_thumbnail, lytContainer, false);
        ImageView imgThumbnail = thumbnailView.findViewById(R.id.img_photo);
        imgThumbnail.setImageBitmap(bitmap);
        lytContainer.addView(thumbnailView, 0);

        // Add listener to listen when user want to remove selected photo
        thumbnailView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lytContainer.removeView(v);
            }
        });
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_images_controller, this);
        ImageView imgAddPhoto = findViewById(R.id.img_add_photo);
        imgAddPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onImagesControllerViewClickListener != null){
                    onImagesControllerViewClickListener.onInsertPhotoClick();
                }
            }
        });
    }

}
