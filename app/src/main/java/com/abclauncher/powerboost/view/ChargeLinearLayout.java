package com.abclauncher.powerboost.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by sks on 2017/1/3.
 */

public class ChargeLinearLayout extends LinearLayout{
    public ChargeLinearLayout(Context context) {
        super(context);
    }

    public ChargeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChargeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setTranslationY(h);
    }
}
