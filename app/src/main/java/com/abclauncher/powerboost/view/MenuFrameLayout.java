package com.abclauncher.powerboost.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by sks on 2016/12/23.
 */

public class MenuFrameLayout extends FrameLayout{
    public MenuFrameLayout(Context context) {
        super(context);
    }

    public MenuFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setVisibility(GONE);
        return true;
    }
}
