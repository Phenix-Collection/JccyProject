package com.abclauncher.powerboost.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sks on 2017/1/13.
 */

public class NormalEditView extends EditText {

    public NormalEditView(Context context) {
        this(context, null);
    }

    public NormalEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeface();
    }

    public NormalEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTypeface();
    }

    private void initTypeface() {
        Typeface fontFace = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Regular.ttf");
        setTypeface(fontFace);
    }
}
