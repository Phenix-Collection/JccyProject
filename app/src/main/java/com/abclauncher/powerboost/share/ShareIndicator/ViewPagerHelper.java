package com.abclauncher.powerboost.share.ShareIndicator;

import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * 简化和ViewPager绑定
  */

public class ViewPagerHelper {
    public static void bind(final ShareIndicator magicIndicator, ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public static final String TAG = "ViewPagerHelper";

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: position"+position+"positionOffset"+positionOffset+"positionOffsetPixels"+positionOffsetPixels);
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position"+position);
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: state"+state);
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }
}
