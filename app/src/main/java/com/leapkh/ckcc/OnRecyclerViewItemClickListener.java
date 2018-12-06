package com.leapkh.ckcc;

import android.view.View;

public interface OnRecyclerViewItemClickListener {

    void onRecyclerViewItemClick(int position);
    void onRecyclerViewOptionItemClick(int position, View optionView);

}
