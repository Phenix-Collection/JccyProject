package com.abclauncher.powerboost.clean.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.abclauncher.powerboost.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by shenjinliang on 16/12/22.
 */

public class BoostAnimView extends View {

    private final static int BOUNCE_SIZE = 6;
    private final static int BG_COLOR = Color.parseColor("#46bf59");
    private int mWidth;
    private int mHeight;
    private Drawable mIcon;
    private int mCenterX;
    private int mCenterY;
    private int mIconSize;
    private int mIconStartX,
            mIconStartY;

    private int lineLength;
    private List<Line> mLines;
    private Paint mLinePaint;
    private Paint mBgPaint;
    private float density;

    private enum iconState{APPEAR, BOUNCE, DISMISS}
    private int slope = 45;
    private int mRadius;
    private String TAG = "BoostAnimView";

    private iconState state = iconState.APPEAR;
    private boolean isStop = false;
    private boolean mIsIconReachToCenter;
    private enum BounceDirection{ LEFT, CENTER_TO_LEFT, CENTER_TO_RIGHT,RIGHT}
    private BounceDirection mBounceDirection;

    public BoostAnimView(Context context) {
        super(context);
        init();
    }

    public BoostAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoostAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    int i = 1;
    int j = 1;
    private void moveIcon(){
        if(state == iconState.APPEAR){
            Log.d(TAG, "moveIcon:APPEAR -------> Y:" + mIconStartY + "X:" + mIconStartX +
            "centerX" + mCenterX + "mCenterY " + mCenterY + " iconSize" + mIconSize);
            int s = (int) (4 + 0.5*i*i *2);
            i++;
            if( (mCenterX - mIconSize / 2) > mIconStartX ){
                mIconStartX += s;
            }

            if( (mCenterY - mIconSize / 2) < mIconStartY){
                mIconStartY -= s ;
            }

            if((mCenterX - mIconSize / 2) <= mIconStartX &&  (mCenterY - mIconSize / 2) >= mIconStartY){
                Log.d(TAG, "moveIcon:APPEAR -------> Y:" + mIconStartY + "X:" + mIconStartX);
                i = 0;
                state = iconState.BOUNCE;
                mIconStartX = mCenterX - mIconSize / 2;
                mIconStartY = mCenterY - mIconSize / 2;
                mBounceDirection = BounceDirection.CENTER_TO_LEFT;
            }
        }

        if(state == iconState.BOUNCE){
            if(mBounceDirection == BounceDirection.CENTER_TO_LEFT) {
                mIconStartX -= Math.sqrt(2) / 2 * BOUNCE_SIZE;
                mIconStartY -= Math.sqrt(2) / 2 * BOUNCE_SIZE;
                mBounceDirection = BounceDirection.LEFT;
            } else if(mBounceDirection == BounceDirection.LEFT){
                mIconStartX = mCenterX - mIconSize / 2;
                mIconStartY = mCenterY - mIconSize / 2;
                mBounceDirection = BounceDirection.CENTER_TO_RIGHT;
            } else if(mBounceDirection == BounceDirection.CENTER_TO_RIGHT){
                mIconStartX += Math.sqrt(2) / 2 * BOUNCE_SIZE;
                mIconStartY += Math.sqrt(2) / 2 * BOUNCE_SIZE;
                mBounceDirection = BounceDirection.RIGHT;
            } else {
                mIconStartX = mCenterX - mIconSize / 2;
                mIconStartY = mCenterY - mIconSize / 2;
                mBounceDirection = BounceDirection.CENTER_TO_LEFT;
            }
        }

        if(state == iconState.DISMISS){
            if(mIconStartX < mWidth){
                mIconStartX += 0.5 * j * j;
            }

            if(mIconStartY > 0 ){
                mIconStartY -= 0.5 * j * j;
            }
            j++;
            if(mIconStartX >= mWidth && mIconStartY <= 0 ){
                isStop = true;
                j = 0;
                Log.d(TAG, "moveIcon:DIssMisss ------->removeCallbacks:" + mIconStartX + "mwidth: ");
                        removeCallbacks(run);
                state = iconState.APPEAR;
            }

        }
    }
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            moveIcon();
            Iterator<Line> iter = mLines.iterator();
            while (iter.hasNext()){
                Line line = iter.next();
                line.move(10);
            }
            if(mLines.size() == 0){
                mLines.add(createRandomLines());
                mLines.add(createRandomLines());
            }
            postInvalidate();
            if(!isStop) {
                Log.d(TAG, "run: moveIcon postDelay---->");
                postDelayed(run, 40);
            }
        }
    };

    private void init(){
        mLines = new ArrayList<>();
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(getResources().getColor(R.color.colorPrimary));
        mLinePaint.setAntiAlias(true);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.parseColor("#22ffffff"));


        density = getResources().getDisplayMetrics().density;
    }

    public void setDisappear(){
        state = iconState.DISMISS;
    }

    public void start(){
        isStop = false;
        post(run);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        Log.d("XXXXXXX", "onSizeChanged: " + w +","+ h);
        mRadius =  Math.min(w, h) / 2;

        mIconSize = w / 4;
        mIconStartX = -mIconSize;
        mIconStartY = h;
        mWidth = w;
        mHeight = h;
        mLines.add(createRandomLines());
        mLines.add(createRandomLines());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       // canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mBgPaint);

        Drawable d = getContext().getResources().getDrawable(R.drawable.ic_boost_icon);

        Iterator<Line> iter = mLines.iterator();
        while (iter.hasNext()){
            Line line = iter.next();
            line.drawLine(canvas,mLinePaint);
            if(line.isReachToBound()){
                iter.remove();
            }
        }

        canvas.save();
        canvas.translate(mIconStartX, mIconStartY);
        d.setBounds(0,0,mIconSize, mIconSize);
        d.draw(canvas);
        canvas.restore();
    }

    private Line createRandomLines(){
        Point start = new Point();
        start.x = (int) (mWidth * 3 / 4 + Math.random() * mWidth / 4);
        start.y = (int) (mHeight * 3 / 4 * Math.random());
       // int lineLength = mWidth / 4 + (int)((mWidth / 10)* Math.random());
        int lineLength = mWidth / 4 + new Random().nextInt((int) (density * 10));
        Point stop = new Point();
        stop.x = (int) (start.x - Math.cos( slope* Math.PI / 180)  * lineLength);
        stop.y = (int) (start.y + Math.sin( slope* Math.PI / 180 ) * lineLength);
        //int lineWidth = (int) (4 + Math.random() * 2);
        float lineWidth = (float) (density + Math.random() * 1.2 * density);

        return new Line(start, stop, lineWidth);
    }

    class Line {
        private Point mStart;
        private Point mStop;
        private float mLineWidth;
        public Line(Point start, Point stop, float lineWidth){
            mStart = start;
            mStop = stop;
            mLineWidth = lineWidth;
        }

        public void drawLine(Canvas canvas, Paint paint){
            Log.d(TAG, "drawLine: " + mStart.x + "," + mStart.y + ", "
                    + mStop.x + ", mStop.y" + mStop.y + "lineLen: " + mLineWidth);
            paint.setStrokeWidth(mLineWidth);
            paint.setColor(Color.WHITE);
            canvas.drawLine(mStart.x, mStart.y, mStop.x, mStop.y,paint);
        }

        public void move(int del){
            mStart.x -= del;
            mStart.y += del;

            mStop.x -= del;
            mStop.y += del;
        }

        public boolean isReachToBound(){
            if( mStart.x < 0
                    || mStop.x > mWidth
                    || mStart.y  < 0
                    || mStart.y > mHeight){
                return true;
            }
            return false;
        }
    }

}
