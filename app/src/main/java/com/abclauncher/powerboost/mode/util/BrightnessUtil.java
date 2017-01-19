package com.abclauncher.powerboost.mode.util;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by sks on 2016/12/27.
 */

public class BrightnessUtil {
    private static final String TAG = "BrightnessUtil";
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

    public boolean isAutoMode(){
        try {
            return Settings.System.getInt(mContentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;

        }
    }

    /**
     * @param curBrightness 手动模式下设置当前的亮度
     */
    public void setCurBrightness(int curBrightness) {
        Settings.System.putInt(mContentResolver,Settings.System.SCREEN_BRIGHTNESS,curBrightness);
    }

    public void setBrightnessPercent(int percent) {
        setCurBrightness((int) (255 * percent * 1.f/ 100));
    }

    /**
     * 获取到当前的亮度值 0-255;
     */
    public int getCurBrightness() {
        return Settings.System.getInt(mContentResolver,
                Settings.System.SCREEN_BRIGHTNESS, 0);
    }

    public int getCurBrightnessPercent(){
        if (getCurBrightness() == 255) return 100;
        Log.d(TAG, "getCurBrightness: " + getCurBrightness());
        Log.d(TAG, "getCurBrightnessPercent: " + (int) ((getCurBrightness() * 100.f/ 255) /10 * 10));
        int percent = (int) ((getCurBrightness() * 100.f/ 255) /10 * 10);
        if ((percent % 10) != 0) {
            return percent / 10 * 10 + 10;
        }
        return (int) ((getCurBrightness() * 100.f/ 255) / 10 * 10);
    }

    public int getRealPercentBrightness(){
        return (int) (getCurBrightness() * 100.f/ 255) % 10 * 10;
    }
}
