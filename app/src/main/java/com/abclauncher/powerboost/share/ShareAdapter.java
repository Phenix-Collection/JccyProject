package com.abclauncher.powerboost.share;

import android.content.ComponentName;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.abclauncher.powerboost.R;


/**
 * Created by shenjinliang on 16/11/18.
 */

class ShareAdapter extends RecyclerView.Adapter<ViewHolder> {

    private int mCount;
    private int mPosition;
    private ShareDialog mShareDialog;

    public ShareAdapter(ShareDialog shareDialog){
        mShareDialog = shareDialog;
    }

    public void setItemCount(int count, int position) {
        mCount = count;
        mPosition = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(View.inflate(mShareDialog.getContext(),
                R.layout.share_dailog_item, null));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int correctPosition = mPosition * 6 + position;
        final ShareItem item = mShareDialog.getShareItemsByPosition(correctPosition);

        holder.mImage.setImageDrawable(item.getAppIcon());
        holder.mText.setText(item.getAppName());
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName shareCN = new ComponentName(item.getPackageName(), item.getClassName());
                ShareHelper.share(mShareDialog.getContext(), shareCN);

                mShareDialog.dismiss();


            }
        });
    }

    @Override
    public int getItemCount() {
        return mCount;
    }
}