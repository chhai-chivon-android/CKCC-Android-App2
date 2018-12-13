package kh.edu.rupp.ckcc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.vayofm.ckcclib.R;

public class ImagesControllerView extends FrameLayout {
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

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_images_controller, this);
    }

}
