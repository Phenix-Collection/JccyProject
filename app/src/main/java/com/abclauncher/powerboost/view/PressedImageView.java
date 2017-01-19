package com.abclauncher.powerboost.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by hyy on 2016/5/11.
 */
public class PressedImageView extends ImageView {
    public PressedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        if (pressed){
            setAlpha(50);
        }else {
            setAlpha(255);
        }
        super.setPressed(pressed);
    }


    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled){
            setAlpha(50);
        }else {
            setAlpha(255);
        }
        super.setEnabled(enabled);
    }
}
