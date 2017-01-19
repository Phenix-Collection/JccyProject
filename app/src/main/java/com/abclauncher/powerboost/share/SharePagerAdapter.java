package com.abclauncher.powerboost.share;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shenjinliang on 16/11/18.
 */

class SharePagerAdapter extends PagerAdapter {

    private ShareDialog mShareDialog;

    public SharePagerAdapter(ShareDialog dialog){
        mShareDialog = dialog;
    }

    @Override
    public int getCount() {
        return mShareDialog.getViewsCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mShareDialog.getViewByPosition(position);
        (container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mShareDialog.getViewByPosition(position));
    }

}
