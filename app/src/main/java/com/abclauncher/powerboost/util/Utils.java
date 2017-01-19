package com.abclauncher.powerboost.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.abclauncher.powerboost.bean.AppInfo;
import com.abclauncher.powerboost.clean.utils.CleanUtil;
import com.abclauncher.powerboost.mode.util.AudioUtil;
import com.abclauncher.powerboost.mode.util.BluetoothUtil;
import com.abclauncher.powerboost.mode.util.BrightnessUtil;
import com.abclauncher.powerboost.mode.util.WifiUtil;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sks on 2016/12/22.
 */

public class Utils {
    private static final String TAG = "Utils";
    private static long sTotalTime;
    private static List<AppInfo> sAllRunningApps = new ArrayList<>();
    private static List<AppInfo> sNonSystemAppList = new ArrayList<>();
    private static HashMap<String, AppInfo> sAllRunningAppsMap = new HashMap<>();
    private static long FULL_USAGE_TIME = 1000 * 60 * 60 * 36;
    private static long WIFI_TIME = 1000 * 60 * 100;
    private static long BRIGHTNESS_TIME = 1000 * 60 * 100;
    private static long BLUETOOTH_TIME = 1000 * 60 * 60;

    public static long getAppProcessTime(int pid) {
        FileInputStream in = null;
        String ret = null;
        try {
            in = new FileInputStream("/proc/" + pid + "/stat");
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            ret = os.toString();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (ret == null) {
            return 0;
        }

        String[] s = ret.split(" ");
        if (s == null || s.length < 17) {
            return 0;
        }

        final long utime = string2Long(s[13]);
        final long stime = string2Long(s[14]);
        final long cutime = string2Long(s[15]);
        final long cstime = string2Long(s[16]);

        return utime + stime + cutime + cstime;
    }

    private static long string2Long(String s) {
        if (s != null) {
            //Log.d(TAG, "string2Long: " + s);
            return Long.valueOf(s);
        }
        return 0;
    }

    private static List<AppInfo> findRunningApps(Context context) {
        sTotalTime = 0;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        Log.d(TAG, "caculateAppBattery: size--->" + runningApps.size());
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo info : runningApps) {
            final long time = getAppProcessTime(info.pid);
            String[] pkgNames = info.pkgList;
            AppInfo appInfo = new AppInfo();
            appInfo.pkgName = pkgNames[0];
            if (context.getPackageName().equals(appInfo.pkgName)) continue;
            appInfo.cpuTime = time;
            sTotalTime += appInfo.cpuTime;
            appInfos.add(appInfo);
        }

        Collections.sort(appInfos, new CustomComparator());
        return appInfos;
    }

    public static List<AppInfo> findRunningAppsLollipop(Context context) {
        sTotalTime = 0;
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        final List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        for (AndroidAppProcess androidAppProcess : processes) {
            AppInfo appInfo = new AppInfo();
            final long time = getAppProcessTime(androidAppProcess.pid);
            appInfo.cpuTime = time;

            appInfo.pkgName = androidAppProcess.getPackageName();
            if (context.getPackageName().equals(appInfo.pkgName)) continue;
            sTotalTime += time;
            appInfos.add(appInfo);
        }
        Collections.sort(appInfos, new CustomComparator());
        return appInfos;
    }

    public static List<AppInfo> getRunningAppList(Context context) {
        List<AppInfo> allApps = new ArrayList<>();
        List<AppInfo> runningApps = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            runningApps = findRunningAppsLollipop(context);
        } else {
            runningApps = findRunningApps(context);
        }
        PackageManager packageManager = context.getPackageManager();

        for (AppInfo appInfo : runningApps) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(appInfo.pkgName, PackageManager.GET_META_DATA);
                appInfo.icon = applicationInfo.loadIcon(packageManager);

                appInfo.appName = (String) applicationInfo.loadLabel(packageManager);
                if (TextUtils.isEmpty(appInfo.appName)) {
                    appInfo.appName = appInfo.pkgName;
                }

                appInfo.isSystemApp = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
                appInfo.percent = format((appInfo.cpuTime * 100.0d) / sTotalTime);
                if (sAllRunningAppsMap.containsKey(appInfo.appName)) {
                    Log.d(TAG, "getRunningAppList: --->" + appInfo.appName);
                    AppInfo appInfo1 = sAllRunningAppsMap.get(appInfo.appName);
                    appInfo1.cpuTime += appInfo.cpuTime;
                    appInfo1.percent = format((appInfo1.cpuTime * 100.0d) / sTotalTime);
                    continue;
                }else {
                    sAllRunningAppsMap.put(appInfo.appName, appInfo);
                }

            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "getRunningAppList: " + e.toString());
                e.printStackTrace();
            }
        }
        allApps.addAll(sAllRunningAppsMap.values());
        Collections.sort(allApps, new CustomComparator());
        return allApps;
    }

    public static void initAllRunningApps(Context context){
        sAllRunningApps.clear();
        sAllRunningAppsMap.clear();
        sAllRunningApps = getRunningAppList(context);
    }

    public static List<AppInfo> getAllRunningApps() {
        return sAllRunningApps;
    }

    public static List<AppInfo> getNonSystemAppList() {
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        for (AppInfo appInfo : sAllRunningApps) {
            if (!appInfo.isSystemApp){
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    /**
     * 使用NumberFormat,保留小数点后两位
     */
    public static String format(double value) {
        Log.d(TAG, "format: " + value);
        if (value < 0.01) value = 0.01;

        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        /*
         * setMinimumFractionDigits设置成2
         *
         * 如果不这么做，那么当value的值是100.00的时候返回100
         *
         * 而不是100.00
         */
        //nf.setMinimumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        /*
         * 如果想输出的格式用逗号隔开，可以设置成true
         */
        nf.setGroupingUsed(false);
        return nf.format(value);
    }


    public static boolean isApplicationInstalled(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName) || null == context) {
            return false;
        }
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.d(TAG, "get packageinfo failed");
        }
        return null != packageInfo;
    }

    /**
     * 打开应用详情
     *
     * @param context
     * @param packageName
     */
    public static void openAppDetails(Context context, String packageName) {

        if (false == isApplicationInstalled(context, packageName)) {
            //Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.parse("package:" + packageName);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }

    public static long getUsageTime(Context context, int percent) {
        if (CleanUtil.shouldCleanMemory(context)){
            long currentTime = (long) (FULL_USAGE_TIME * percent * 1.f / 100);
            if (WifiUtil.getInstance(context).getWifiOpened()) {
                currentTime = (long) (currentTime - WIFI_TIME * 1.f * percent / 100);
            } else {
                currentTime = (long) (currentTime + WIFI_TIME * 1.f * percent / 100);
            }
            if (AudioUtil.getInstance(context).getRingMode() != AudioManager.RINGER_MODE_SILENT) {
                currentTime = (long) (currentTime - WIFI_TIME * 1.f * percent / 100);
            }else {
                currentTime = (long) (currentTime + WIFI_TIME * 1.f * percent / 100);
            }
            int brightness = BrightnessUtil.getInstance(context).getCurBrightnessPercent() - 20;
            long brightnessTime = (long) (BRIGHTNESS_TIME * brightness * 1.f /100);
            currentTime = currentTime - brightnessTime;
            if (BluetoothUtil.getInstance(context).getBluetoothStatus()) {
                currentTime = (long) (currentTime - BLUETOOTH_TIME * percent * 1.f /100);
            } else {
                currentTime = (long) (currentTime +  BLUETOOTH_TIME * percent * 1.f /100);
            }

            SettingsHelper.setUsageTime(context, currentTime);
            return currentTime;
        } else {
            return SettingsHelper.getUsageTime(context);
        }
    }

    public static String getUsageHourValue(long time){
        int hour = (int) (time * 1.f/ (1000 * 60 * 60));
        if (hour == 0) {
            return "00";
        } else if (hour < 10){
            return "0" + hour;
        } else {
            return hour + "";
        }
    }

    public static String getUsageMinutesValue(long time){
        int hour = (int) (time * 1.f/ (1000 * 60 * 60));
        int mins = (int) ((time - hour * 1000 * 60 * 60) / (1000 * 60));
        if (mins == 0) {
            return "00";
        } else if (mins < 10){
            return "0" + mins;
        } else {
            return mins + "";
        }
    }

    public static void addShortCutScreen(Context context, String appName, Intent actionIntent,
                                         boolean allowRepeat, Bitmap icon){
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", allowRepeat);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
        context.sendBroadcast(intent);
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }
}
