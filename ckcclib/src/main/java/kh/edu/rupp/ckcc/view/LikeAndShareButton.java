package kh.edu.rupp.ckcc.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vayofm.ckcclib.R;

public class LikeAndShareButton extends FrameLayout {

    private TextView txtViewCount;

    private OnLikeAndShareButtonClickListener onLikeAndShareButtonClickListener;

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

    public void setOnLikeAndShareButtonClickListener(OnLikeAndShareButtonClickListener onLikeAndShareButtonClickListener) {
        this.onLikeAndShareButtonClickListener = onLikeAndShareButtonClickListener;
    }

    public void setViewCount(int viewCount, boolean showViewCountLabel) {
        if (showViewCountLabel) {
            txtViewCount.setText("Views: " + viewCount);
        } else {
            txtViewCount.setText("" + viewCount);
        }
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_like_and_share_button, this);
        txtViewCount = findViewById(R.id.txt_view_count);

        TextView txtShare = findViewById(R.id.txt_share);
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeAndShareButtonClickListener != null) {
                    onLikeAndShareButtonClickListener.onShareButtonClick();
                }
            }
        });
        TextView txtLike = findViewById(R.id.txt_like);
        txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeAndShareButtonClickListener != null) {
                    onLikeAndShareButtonClickListener.onLikeButtonClick();
                }
            }
        });
    }

    private void initCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LikeAndShareButton, 0, 0);
        int viewCount = typedArray.getInt(R.styleable.LikeAndShareButton_view_count, 0);
        boolean showLabel = typedArray.getBoolean(R.styleable.LikeAndShareButton_show_view_count_label, true);
        setViewCount(viewCount, showLabel);
        typedArray.recycle();
    }


}
