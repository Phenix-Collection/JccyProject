package com.abclauncher.powerboost.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.abclauncher.powerboost.R;


/**
 * Created by sks on 2016/12/9.
 */
public class BatteryProgress extends View {

    private static final String TAG = "HorizontalProgress";
    private Paint mBgPaint, mProgressPaint;
    private RectF mBgBounds, mProgressBounds, mLeftTopBounds, mLeftBottomBounds, mRightBottomBounds, mRightTopBounds;
    private float mRadius;
    private float density;
    private float mProgress;
    private Path mBgPath, mProgressPath;
    private int mBgColor, mProgressColor;
    private Paint mStrokePaint;
    private float mStrokeWidth;
    private int mStrokeColor;
    private int WIDTH, HEIGHT;

    public BatteryProgress(Context context) {
        super(context);
    }

    public BatteryProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        density = getContext().getResources().getDisplayMetrics().density;

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BatteryProgress);
        mProgress = typedArray.getInteger(R.styleable.BatteryProgress_battery_progress, 100);
        mRadius = typedArray.getDimension(R.styleable.BatteryProgress_battery_radius, 2);
        mBgColor = typedArray.getColor(R.styleable.BatteryProgress_battery_bg_color, Color.parseColor("#22ffffff"));
        mProgressColor = typedArray.getColor(R.styleable.BatteryProgress_battery_progress_color, Color.parseColor("#F2FAFE"));
        mStrokeWidth = typedArray.getDimension(R.styleable.BatteryProgress_stroke_width, 2);
        mStrokeColor = typedArray.getColor(R.styleable.BatteryProgress_battery_stroke_color, Color.parseColor("#71ffffff"));
        typedArray.recycle();

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);

        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setColor(mStrokeColor);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);

        mBgBounds = new RectF();
        mProgressBounds = new RectF();
        mLeftTopBounds = new RectF();
        mLeftBottomBounds = new RectF();
        mRightBottomBounds = new RectF();
        mRightTopBounds = new RectF();

        mBgPath = new Path();
        mProgressPath = new Path();

    }

    public BatteryProgress(Context context, AttributeSet attrs, int defStyleAttr) {
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


        //左下角正方形
        mLeftBottomBounds.left = mBgBounds.left;
        mLeftBottomBounds.top = mBgBounds.bottom - mRadius * 2;
        mLeftBottomBounds.bottom = mBgBounds.bottom;
        mLeftBottomBounds.right = mBgBounds.left + 2* mRadius;

        //左上角正方形
        mLeftTopBounds.left = mBgBounds.left;
        mLeftTopBounds.top = 0;
        mLeftTopBounds.bottom = 2 * mRadius;
        mLeftTopBounds.right = mBgBounds.left + 2* mRadius;

        initBgPath();
    }

    private void initBgPath() {
        mBgPath.moveTo(mRadius, 0);
        mBgPath.lineTo(mBgBounds.right - mRadius, 0);
        mBgPath.arcTo(mRightTopBounds, -90, 90, false);
        mBgPath.lineTo(mBgBounds.right, mBgBounds.bottom - mRadius);
        mBgPath.arcTo(mRightBottomBounds, 0, 90, false);
        mBgPath.lineTo(mBgBounds.left + mRadius, mBgBounds.bottom);
        mBgPath.arcTo(mLeftBottomBounds, 90, 90, false);
        mBgPath.lineTo(0, mBgBounds.top + mRadius);
        mBgPath.arcTo(mLeftTopBounds, 180, 90, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initProgressBounds();
        initProgressPath();
        canvas.drawPath(mBgPath, mBgPaint);
        canvas.drawPath(mProgressPath, mProgressPaint);

    }

    private void initProgressBounds() {
        mProgressBounds.left = 0;
        mProgressBounds.top = 0;
        mProgressBounds.right = mBgBounds.right * mProgress / 100f;
        Log.d(TAG, "initProgressBounds: right-->" + mProgressBounds.right);
        mProgressBounds.bottom = mBgBounds.bottom;
        if(mProgress <= 30 ) {
            mProgressPaint.setShader(new LinearGradient(0,0,mBgBounds.right,0,0Xffff9233,0Xfffff133, Shader.TileMode.REPEAT));
        } else {
            mProgressPaint.setShader(new LinearGradient(0,0,mBgBounds.right,0,0Xff68c9ff,0Xff7dfdff, Shader.TileMode.REPEAT));
        }

    }

    private void initProgressPath() {

        mProgressPath.reset();
        mProgressPath.moveTo(mRadius, 0);
        if (mProgress == 100){
            mProgressPath.lineTo(mBgBounds.right - mRadius, 0);
            mProgressPath.arcTo(mRightTopBounds, -90, 90, false);
            mProgressPath.lineTo(mBgBounds.right, mBgBounds.bottom - mRadius);
            mProgressPath.arcTo(mRightBottomBounds, 0, 90, false);
        }else {
            mProgressPath.lineTo(mProgressBounds.right, 0);
            mProgressPath.lineTo(mProgressBounds.right, mProgressBounds.bottom );
        }
        mProgressPath.lineTo(mProgressBounds.left + mRadius, mProgressBounds.bottom);
        mProgressPath.arcTo(mLeftBottomBounds, 90, 90, false);
        mProgressPath.lineTo(0, mProgressBounds.top + mRadius);
        mProgressPath.arcTo(mLeftTopBounds, 180, 90, false);
    }

    public float getProgress(){
        return mProgress;
    }

    public void setProgress(float progress){
        Log.d(TAG, "setProgress: " + progress);
        mProgress = progress;
        invalidate();
    }
}
