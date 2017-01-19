package com.abclauncher.powerboost.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by sks on 2016/12/9.
 */
public class BatteryHeadView extends View {

    private static final String TAG = "HorizontalProgress";
    private Paint mBgPaint, mProgressPaint;
    private RectF mBgBounds, mProgressBounds, mLeftTopBounds, mLeftBottomBounds, mRightBottomBounds, mRightTopBounds;
    private float mRadius;
    private float density;
    private float mProgress;
    private Path mBgPath, mProgressPath;
    private int mBgColor, mProgressColor;
    private Paint mStrokePaint;

    private int WIDTH, HEIGHT;

    public BatteryHeadView(Context context) {
        super(context);
    }

    public BatteryHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        density = getContext().getResources().getDisplayMetrics().density;
        mRadius = 2.5f * density;
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(Color.parseColor("#44ffffff"));

        mBgBounds = new RectF();
        mLeftTopBounds = new RectF();
        mLeftBottomBounds = new RectF();
        mRightBottomBounds = new RectF();
        mRightTopBounds = new RectF();

        mBgPath = new Path();

    }

    public BatteryHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        mBgBounds.left = 0;
        mBgBounds.right = w;
        mBgBounds.top = 0;
        mBgBounds.bottom = h;

        //右上角正方形
        mRightTopBounds.left = mBgBounds.right - mRadius * 2;
        mRightTopBounds.top = 0;
        mRightTopBounds.bottom = mRadius * 2;
        mRightTopBounds.right = mBgBounds.right;

        //右下角正方形
        mRightBottomBounds.left = mBgBounds.right - mRadius * 2;
        mRightBottomBounds.top = mBgBounds.bottom - mRadius * 2;
        mRightBottomBounds.bottom = mBgBounds.bottom;
        mRightBottomBounds.right = mBgBounds.right;


      /*  //左下角正方形
        mLeftBottomBounds.left = mBgBounds.left;
        mLeftBottomBounds.top = mBgBounds.bottom - mRadius * 2;
        mLeftBottomBounds.bottom = mBgBounds.bottom;
        mLeftBottomBounds.right = mBgBounds.left + 2* mRadius;

        //左上角正方形
        mLeftTopBounds.left = mBgBounds.left;
        mLeftTopBounds.top = 0;
        mLeftTopBounds.bottom = 2 * mRadius;
        mLeftTopBounds.right = mBgBounds.left + 2* mRadius;*/

        initBgPath();
    }

    private void initBgPath() {
        mBgPath.moveTo(0, 0);
        mBgPath.lineTo(mBgBounds.right - mRadius, 0);
        mBgPath.arcTo(mRightTopBounds, -90, 90, false);
        mBgPath.lineTo(mBgBounds.right, mBgBounds.bottom - mRadius);
        mBgPath.arcTo(mRightBottomBounds, 0, 90, false);
        mBgPath.lineTo(mBgBounds.left, mBgBounds.bottom);
        mBgPath.lineTo(0, mBgBounds.top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mBgPath, mBgPaint);
    }

}
