package com.abclauncher.powerboost.locker.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;



import butterknife.ButterKnife;

/**
 * Created by sks on 2016/11/17.
 */

public abstract class BaseLockerActivity extends AppCompatActivity implements SwipeBackLayout.SwipeBackListener {


    public abstract int getLayoutId();
    public abstract void initViews();
    private SwipeBackLayout swipeBackLayout;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        swipeBackLayout.addView(view);
    }
    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setOnSwipeBackListener(this);
        container.addView(swipeBackLayout);
        return container;
    }

    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        swipeBackLayout.setDragEdge(dragEdge);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        setContentView(layoutId);
        getSwipeBackLayout().setEnablePullToBack(false);
        ButterKnife.inject(this);
        initViews();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
