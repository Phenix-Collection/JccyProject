package com.abclauncher.powerboost.mode.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by sks on 2016/12/27.
 */

public class HapticFeedbackUtil {
    private static HapticFeedbackUtil sHapticFeedbackUtil;
    private Context mContext;
    private final ContentResolver mContentResolver;

    public static HapticFeedbackUtil getInstance(Context context) {
        if (sHapticFeedbackUtil == null){
            sHapticFeedbackUtil = new HapticFeedbackUtil(context);
        }
        return sHapticFeedbackUtil;
    }

    private HapticFeedbackUtil(Context context){
        mContext = context;
        mContentResolver = context.getContentResolver();

    }

    public void setHapticFeedbackEnable(boolean enable) {
        if (enable){
            Settings.System.putInt(mContentResolver, android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
        }else {
            Settings.System.putInt(mContentResolver, android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) ;
        }
    }

    public boolean getHapticFeedbackEnable(){
        return  Settings.System.getInt(mContentResolver, android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) != 0;
    }
}
