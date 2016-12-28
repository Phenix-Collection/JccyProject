package com.abclauncher.powerboost.mode.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by sks on 2016/12/27.
 */

public class ScreenOutUtil {
    public static final int s15 = 15 * 1000;
    public static final int s30 = 30 * 1000;
    public static final int m1 = 60 * 1000;
    public static final int m5 = 5 * 60 * 1000;
    public static final int m10 = 10 * 60 * 1000;
    public static int[] times = new int[]{s15, s30, m1, m5, m10};
    private static ScreenOutUtil sScreenOutUtil;
    private final ContentResolver mContentResolver;
    private Context mContext;

    public static ScreenOutUtil getInstance(Context context) {
        if (sScreenOutUtil == null){
            sScreenOutUtil = new ScreenOutUtil(context);
        }
        return sScreenOutUtil;
    }

    private ScreenOutUtil(Context context){
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }

    public void setScreenOutTime(int screenOutTime){
        Settings.System.putInt(mContentResolver,Settings.System.SCREEN_OFF_TIMEOUT,screenOutTime);
    }

    public int getScreenOutTime(){
        return Settings.System.getInt(mContentResolver,Settings.System.SCREEN_OFF_TIMEOUT, s15);
    }

    public int getScreenOutTimePercent(int screenTimeOut){
        int percent = 0;
        if (screenTimeOut < times[0]) {
            return 0;
        } else if (screenTimeOut > times[times.length -1]) {
            return times.length - 1;
        } else {
            for (int i = 0; i < times.length; i++) {
                if (screenTimeOut == times[i]) return i;
            }
            for (int i = 0; i < times.length; i++) {
                if (screenTimeOut > times[i] && screenTimeOut < times[i+1]){
                    return i;
                }
            }
            return percent;
        }
    }

    public int getScreenTimeOutByPercent(int percent) {
        return times[percent];
    }
}
