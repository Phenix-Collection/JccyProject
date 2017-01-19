package com.abclauncher.powerboost.share;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abclauncher.powerboost.R;


/**
 * Created by shenjinliang on 16/11/18.
 */

class ViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout mRootView;
    public ImageView mImage;
    public TextView mText;

    public ViewHolder(View itemView) {
        super(itemView);
        mRootView = (LinearLayout) itemView.findViewById(R.id.share_dialog);
        mImage = (ImageView) itemView.findViewById(R.id.image);
        mText = (TextView) itemView.findViewById(R.id.text);

    }
}
