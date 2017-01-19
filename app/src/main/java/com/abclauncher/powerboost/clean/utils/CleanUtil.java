package com.abclauncher.powerboost.clean.utils;

import android.content.Context;

import com.abclauncher.powerboost.util.SettingsHelper;

/**
 * Created by sks on 2017/1/4.
 */

public class CleanUtil {
    private static final long  THRESHOLD_TIME = 1000 * 60 * 2;

    public static boolean shouldCleanMemory(Context context){
        if (SettingsHelper.getLastCleanTime(context) == 0) return true;
        if (System.currentTimeMillis() - SettingsHelper.getLastCleanTime(context) > THRESHOLD_TIME)return true;
        return false;
    }
}
