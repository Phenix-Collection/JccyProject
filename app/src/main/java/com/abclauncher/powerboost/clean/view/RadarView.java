package com.abclauncher.powerboost.clean.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.abclauncher.powerboost.R;


/**
 * Created by shenjinliang on 16/12/21.
 */

public class RadarView extends View {
    private int mCenterX;
    private int mCenterY;
    private int mRadarRadius;
    private float mBorderWidth;

    private int mCircleBgColor = Color.parseColor("#46bf59");
    private int mLayerColor = Color.parseColor("#F5F5F5");
    private int mCircleColor = Color.parseColor("#bbffffff");
    private int mShaderStartColor = Color.parseColor("#00ffffff");
    private int mShaderEndColor = Color.parseColor("#59ffffff");
    private int radarLineColor = Color.parseColor("#33FFFFFF");
    private int mDefaultWidth, mDefaultHeight;
    private Matrix scanMatrix;

    // draw radar bg circle
    private Paint mBgPaint;

    // draw radar outer circle
    private Paint mOuterCirclePaint;

    // draw radar  scanner
    private Paint mRadarPaint;
    //draw radar line;
    private Paint mRadarLinePaint;

    //radar circle line
    private Paint mStrokeCirclePaint;
    private Paint mWhiteLinePaint;

    private boolean isScan = false;


    public RadarView(Context context) {
        super(context);
        init(null, context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.RadarView);
            mCircleBgColor = ta.getColor(R.styleable.RadarView_circleBackGroundColor, getResources().getColor(R.color.colorPrimary));
            mCircleColor = ta.getColor(R.styleable.RadarView_circleColor, mCircleColor);
            mLayerColor = ta.getColor(R.styleable.RadarView_layerColor, mLayerColor);

            mShaderStartColor = ta.getColor(R.styleable.RadarView_radarShaderStartColor, mShaderStartColor);
            mShaderEndColor = ta.getColor(R.styleable.RadarView_radarShaderEndColor, mShaderEndColor);
            radarLineColor = ta.getColor(R.styleable.RadarView_radarLineColor, radarLineColor);

            mBorderWidth = ta.getDimension(R.styleable.RadarView_RadarBorderWidth, mBorderWidth);

            ta.recycle();
        }

        mDefaultWidth = context.getResources().getDimensionPixelSize(R.dimen.default_radar_width);
        mDefaultHeight = context.getResources().getDimensionPixelSize(R.dimen.default_radar_height);

        initPaint();
        scanMatrix = new Matrix();
    }

    private void initPaint() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);

        mOuterCirclePaint = new Paint(mBgPaint);
        mBgPaint.setColor(mCircleBgColor);
        mOuterCirclePaint.setColor(mCircleColor);
        mOuterCirclePaint.setStyle(Paint.Style.FILL);

        mRadarPaint = new Paint();
        mRadarPaint.setAntiAlias(true);

        mRadarLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadarLinePaint.setStrokeWidth(3);
        mRadarLinePaint.setColor(radarLineColor);

        mStrokeCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokeCirclePaint.setColor(radarLineColor);
        mStrokeCirclePaint.setStyle(Paint.Style.STROKE);
        mStrokeCirclePaint.setStrokeWidth(.8f * density);

        mWhiteLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWhiteLinePaint.setColor(radarLineColor);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        Log.d("XXXXXXX", "onSizeChanged: " + w +","+ h);
        mRadarRadius = (int) (Math.min(w, h) / 2 - 2 * mBorderWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius + mBorderWidth, mOuterCirclePaint);
        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius, mBgPaint);

        canvas.drawLine(mCenterX , mCenterY - mRadarRadius, mCenterX, mCenterY + mRadarRadius, mWhiteLinePaint);
        canvas.drawLine(mCenterX - mRadarRadius, mCenterY, mCenterX + mRadarRadius, mCenterY, mWhiteLinePaint);

        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius / 3, mStrokeCirclePaint);
        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius*2 / 3, mStrokeCirclePaint);

        SweepGradient shader = new SweepGradient(mCenterX, mCenterY, mShaderStartColor, mShaderEndColor);
        mRadarPaint.setShader(shader);
        canvas.concat(scanMatrix);
        canvas.drawLine(mCenterX, mCenterY, mCenterX + mRadarRadius, mCenterY, mRadarLinePaint);
        canvas.drawCircle(mCenterX, mCenterY, mRadarRadius, mRadarPaint);
    }

    private int startScanDegree = 0;
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            scanMatrix.reset();
            startScanDegree += 10;
            scanMatrix.postRotate(startScanDegree, mCenterX, mCenterY);

            postInvalidate();

            if(isScan) {
                postDelayed(run, 30);
            }

        }
    };

    public void runAnim(){
        isScan = true;
        post(run);
    }

    public void cancelAnim(){
        isScan = false;
    }

    public void restartAnim(){
        isScan = true;
        post(run);
    }

    public boolean isAnimRunning(){
        return isScan;
    }
}
