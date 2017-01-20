package com.abclauncher.powerboost.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.abclauncher.powerboost.R;


/**
 * Created by sks on 2017/1/9.
 */

public class CustomFrameLayout extends FrameLayout{

    private boolean mShowBubble;
    private Paint paint;
    private RectF rectF;
    private int mStartColor, mEndColor;

    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFrameLayout);
        mStartColor = typedArray.getColor(R.styleable.CustomFrameLayout_start_color, getResources().getColor(R.color.blue_start_color));
        mEndColor = typedArray.getColor(R.styleable.CustomFrameLayout_end_color, getResources().getColor(R.color.blue_end_color));
        mShowBubble = typedArray.getBoolean(R.styleable.CustomFrameLayout_bubble_show, true);
        typedArray.recycle();
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        rectF = new RectF();
        rectF.left = 0;
        rectF.top = 0;
        rectF.right = w;
        rectF.bottom = h;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        LinearGradient linearGradient = new LinearGradient(0, getMeasuredHeight(), 0, 0, mStartColor,
                mEndColor, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        canvas.drawRect(rectF, paint);
        if (mShowBubble) {
            Drawable drawable = getResources().getDrawable(R.drawable.bg_bubble);
            drawable.setBounds((int)rectF.left, (int)rectF.top, (int)rectF.right, (int)rectF.bottom);
            drawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    public void setShowBubble(boolean value){
        mShowBubble = value;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setStartColorAndEndColor(int startColor, int endColor) {
        mStartColor = startColor;
        mEndColor = endColor;
        invalidate();
    }

    public int getStartColor(){
        return mStartColor;
    }


    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }
}
