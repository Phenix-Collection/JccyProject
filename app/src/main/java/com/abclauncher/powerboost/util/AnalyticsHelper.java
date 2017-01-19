package com.abclauncher.powerboost.util;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by miao on 2016/4/28.
 */

public class AnalyticsHelper {

    private final static String TAG = "AnalyticsHelper";
    private static Context sAppContext = null;
    private static Tracker mTracker;

    public static void sendScreenView(String screenName) {
        if (isInitialized()) {
            mTracker.setScreenName(screenName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    public static void sendEvent(String category, String action, String label, long value,
                                 HitBuilders.EventBuilder eventBuilder) {
        if(isInitialized()) {
            mTracker.send(eventBuilder
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        }
    }


    public static void sendEvent(String category, String action, String label) {
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        sendEvent(category, action, label, 0, eventBuilder);
    }


    public static void sendEvent(String category, String action){
        sendEvent(category, action, null);
    }

    private static boolean isInitialized() {
        return sAppContext != null && mTracker != null;
    }

    private static synchronized void initializeAnalyticsTracker(Context applicationContext) {
        if (mTracker == null){
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(applicationContext);
            mTracker = analytics.newTracker("UA-86668289-1"); // 升级gms到3.0.0版本编译时提示找不到xml by xq
        }
    }

    public static void prepareAnalytics(Context applicationContext) {
        sAppContext = applicationContext;
        initializeAnalyticsTracker(sAppContext);
    }
}