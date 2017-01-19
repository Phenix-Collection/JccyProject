package com.abclauncher.powerboost.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sks on 2017/1/13.
 */

public class LightTextView extends TextView {

    public LightTextView(Context context) {
        this(context, null);
    }

    public LightTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initTypeface();
    }

    public LightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTypeface();
    }

    private void initTypeface() {
        Typeface fontFace = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Light.ttf");
        setTypeface(fontFace);
    }
}
