package com.abclauncher.powerboost.locker.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.abclauncher.powerboost.R;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sks on 2016/11/17.
 */

public class LockerUtils {
    private LockerUtils(){}
    private static long USB_ON_GAP = 50L;
    private static long USB_ON_TIME = 12288400L;
    private static long AC_ON_GAP = 50L;
    private static long AC_ON_TIME = 3967350L;
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    /**
     * 判断坐标是否在target View 中
     *
     * @param view
     * @param ev
     * @return
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }
    public static String getNowTimeStr(Context context) {
        Date date = new Date();
        DateFormat mTimeFormat;
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            mTimeFormat = new SimpleDateFormat("HH:mm");
        } else {
            mTimeFormat = new SimpleDateFormat("h:mm");
        }
        String timeValue = mTimeFormat.format(date);
        return timeValue;
    }

    public static String getDateStr(Context context) {
        Locale.setDefault(Locale.ENGLISH);
        String dateValue = DateUtils.formatDateTime(context.getApplicationContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
        return dateValue;
    }
    public static int getBatteryPercent(int level, int scale) {
        DecimalFormat formatter = new DecimalFormat();
        formatter.applyPattern("#");
        String levelStr = formatter.format((double) ((float) level / (float) scale * 100.0F));
        Locale.setDefault(Locale.ENGLISH);
        return getIntPercent(levelStr);
    }
    public static int getIntPercent(String progressStr) {
        int percent;
        try {
            String e = "[^0-9]";
            Pattern pattern = Pattern.compile(e);
            Matcher matcher = pattern.matcher(progressStr);
            String subStr = matcher.replaceAll("").trim();
            percent = Integer.parseInt(subStr);
        } catch (Exception var6) {
            percent = 10;
        }
        return percent;
    }
    public static long getFullChargeTime(Intent intent, int percent) {
        long milliseconds = SystemClock.elapsedRealtime() / 1000L;
        long[] savedValues = new long[2];
        long[] definedValues = new long[2];
        int plugged = intent.getIntExtra("plugged", 0);
        long leftTime = 0L;
        switch (plugged) {
            case 0:
                return -1L;
            case 1:
                savedValues[0] = (long) percent;
                savedValues[1] = milliseconds;
                definedValues[0] = AC_ON_GAP;
                definedValues[1] = AC_ON_TIME;
                break;
            case 2:
                savedValues[0] = (long) percent;
                savedValues[1] = milliseconds;
                definedValues[0] = USB_ON_GAP;
                definedValues[1] = USB_ON_TIME;
        }

        long totalLevelGap = savedValues[0] + definedValues[0];
        long totalTimeGap = savedValues[1] + definedValues[1];
        double curChargingRate = 0.0D;
        if (totalLevelGap > 0L) {
            curChargingRate = (double) totalTimeGap / (double) totalLevelGap;
        }

        int scale = intent.getIntExtra("scale", 0);
        leftTime = (long) (curChargingRate * (double) (scale - percent)) / 1000L;
        return leftTime;
    }


    public static String getCharingHourValueStr(Context context, Intent intent, int percent){
        int changeTime = (int) getFullChargeTime(intent, percent);
        int time = changeTime / 60;
        int hour = time / 60;
        if (hour == 0) {
            return "00";
        }else if (hour < 10){
            return "0" + hour;
        } else{
            return hour + "";
        }
    }

    public static String getCharingMinutesValueStr(Context context, Intent intent, int percent){
        int changeTime = (int) getFullChargeTime(intent, percent);
        int time = changeTime / 60;
        int minute = time % 60;
        if (minute == 0) {
            return "00";
        }else if (minute < 10){
            return "0" + minute;
        } else{
            return minute + "";
        }
    }
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
    @TargetApi(14)
    public static boolean hasNavBar(Context context) {
        final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }
        }
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool",
                "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag (see static block)
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }
    public static void setMargins (View v, int left, int top, int right, int bottom) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            v.requestLayout();
        }
    }
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        try {
            final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
            final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";

            Resources res = context.getResources();
            final boolean mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (hasNavBar(context)) {
                    String key;
                    if (mInPortrait) {
                        key = NAV_BAR_HEIGHT_RES_NAME;
                    } else {
                        key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                    }
                    return getInternalDimensionSize(res, key);
                }
            }
        }catch (Exception e){
            return 0;
        }
        return result;
    }
    public static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
