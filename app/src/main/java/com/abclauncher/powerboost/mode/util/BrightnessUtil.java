package com.abclauncher.powerboost.mode.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * Created by sks on 2016/12/27.
 */

public class BrightnessUtil {
    private static BrightnessUtil sBrightnessUtil;
    private final ContentResolver mContentResolver;
    private Context mContext;
    public static final int AUTO_BRIGHTNESS = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
    public static final int MANUAL_BRIGHTNESS = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;


    public static BrightnessUtil getInstance(Context context) {
        if (sBrightnessUtil == null){
            sBrightnessUtil = new BrightnessUtil(context);
        }
        return sBrightnessUtil;
    }

    private BrightnessUtil(Context context){
        mContext = context;
        mContentResolver = mContext.getContentResolver();
    }
    /**
     * 设置当前的亮度模式
     * @param brightMode 亮度模式
     */
    public void setCurBrightnessMode(int brightMode) {
        switch (brightMode){
            case AUTO_BRIGHTNESS:
                try {
                    Settings.System.putInt(mContentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case MANUAL_BRIGHTNESS:
                try {
                    Settings.System.putInt(mContentResolver,
                            Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * @param curBrightness 手动模式下设置当前的亮度
     */
    private void setCurBrightness(int curBrightness) {
        Settings.System.putInt(mContentResolver,Settings.System.SCREEN_BRIGHTNESS,curBrightness);
    }

    /**
     * 获取到当前的亮度值 0-255;
     */
    private int getCurBrightness() {
        return Settings.System.getInt(mContentResolver,
                Settings.System.SCREEN_BRIGHTNESS, 0);
    }
}
