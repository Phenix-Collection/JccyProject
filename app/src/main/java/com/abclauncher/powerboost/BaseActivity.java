package com.abclauncher.powerboost;

import android.support.v7.app.AppCompatActivity;

import com.abclauncher.powerboost.util.statusbar_util.StatusBarUtil;
import com.facebook.appevents.AppEventsLogger;

/**
 * Created by sks on 2017/1/9.
 */

public class BaseActivity extends AppCompatActivity {

    private AppEventsLogger mLogger;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar();
        mLogger = AppEventsLogger.newLogger(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}
