package com.abclauncher.powerboost.share;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


import com.abclauncher.powerboost.R;
import com.abclauncher.powerboost.share.ShareIndicator.ShareIndicator;
import com.abclauncher.powerboost.share.ShareIndicator.ViewPagerHelper;
import com.abclauncher.powerboost.share.ShareIndicator.navigator.ScaleCircleNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 16/9/13.
 */

public class ShareDialog extends Dialog implements View.OnClickListener, DialogInterface.OnKeyListener {

    private List<ShareItem> mShareItems = new ArrayList<>();
    private Context mContext;

    private ViewPager mViewPager;
    private int countPager = 0;
    private ArrayList<RecyclerView> mViews;
    private ShareIndicator mShareIndicator;
    private TextView mTextCancel;


    public ShareDialog(Context context){
        super(context, R.style.ShareDialog);
        mContext = context;
        setOnKeyListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.select_share_layout);
        initViewAndDate();
    }

    private void initViewAndDate() {
        mTextCancel = (TextView) findViewById(R.id.share_cancel);
        mTextCancel.setOnClickListener(this);
        mShareIndicator = (ShareIndicator) findViewById(R.id.share_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mShareItems = ShareHelper.getSortShareItems(mContext);

        mViews = new ArrayList<>();
        countPager = mShareItems.size() / 6;
        if (countPager > 0) {
            for (int i = 0; i < countPager; i++) {
                ShareAdapter shareAdapter = new ShareAdapter(ShareDialog.this);
                shareAdapter.setItemCount(6, i);
                RecyclerView recyclerView = new RecyclerView(getContext());
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
                recyclerView.setAdapter(shareAdapter);
                mViews.add(recyclerView);
            }
        }

        if (mShareItems.size() % 6 > 0) {
            ShareAdapter shareAdapter = new ShareAdapter(ShareDialog.this);
            shareAdapter.setItemCount(mShareItems.size() % 6, countPager);
            RecyclerView recyclerView = new RecyclerView(getContext());
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setAdapter(shareAdapter);
            mViews.add(recyclerView);
            countPager++;
        }

        mViewPager.setAdapter(new SharePagerAdapter(ShareDialog.this));

        ScaleCircleNavigator scaleCircleNavigator = new ScaleCircleNavigator(getContext());
        scaleCircleNavigator.setCircleCount(countPager);
        scaleCircleNavigator.mSelectedCircleColor = getContext().getResources().getColor(R.color.share_indicator_color);
        mShareIndicator.setNavigator(scaleCircleNavigator);
        ViewPagerHelper.bind(mShareIndicator, mViewPager);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.share_cancel) {
            dismiss();
        }
    }

    public ShareItem getShareItemsByPosition(int position){
        return mShareItems.get(position);
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            dismiss();
            return true;
        }
        return false;
    }

    public int getViewsCount(){
        if(mViews == null) {
            return 0;
        } else {
            return mViews.size();
        }
    }

    public View getViewByPosition(int position){
        return mViews.get(position);
    }

}
