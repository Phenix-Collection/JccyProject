package com.abclauncher.powerboost;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.abclauncher.powerboost.util.AnalyticsHelper;
import com.abclauncher.powerboost.util.SettingsHelper;
import com.abclauncher.powerboost.util.Utils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.crash.FirebaseCrash;

import io.branch.referral.Branch;

/**
 * Created by Administrator on 2016/5/3.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AnalyticsHelper.prepareAnalytics(getApplicationContext());
        Branch.getAutoInstance(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Thread.setDefaultUncaughtExceptionHandler(restartHandler);

    }

    private Thread.UncaughtExceptionHandler restartHandler= new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            FirebaseCrash.report(ex);
        }

    };


}
