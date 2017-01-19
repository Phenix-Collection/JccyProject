package com.abclauncher.powerboost.clean.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;


import com.abclauncher.powerboost.clean.bean.AppProcessInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kim on 2016/10/25.
 */
public class AppUtils {

    private static final String TAG = "AppUtils";
    private static float mIdleCpu;

    public static String getTop10() throws IOException {
        final Process m_process = Runtime.getRuntime().exec("/system/bin/top -n 1");

        final StringBuilder sbread = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process.getInputStream()), 8192);

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sbread.append(line + " " + "\n");
        }

        List<String[]> strings = parseProcessRunningInfo(sbread.toString());
        for (int i = 0; i < strings.size(); i++) {
//            Log.d(TAG, "gettop: " + strings.get(i). ();
        }

        return sbread.toString();

    }


    /**
     * 描述：解析数据.
     *
     * @param info User 39%, System 17%, IOW 3%, IRQ 0% PID PR CPU% S #THR VSS
     *             RSS PCY UID Name 31587 0 39% S 14 542288K 42272K fg u0_a162
     *             cn.amsoft.process 313 1 17% S 12 68620K 11328K fg system
     *             /system/bin/surfaceflinger 32076 1 2% R 1 1304K 604K bg
     *             u0_a162 /system/bin/top
     * @return
     */
    public static List<String[]> parseProcessRunningInfo(String info) {
        List<String[]> processList = new ArrayList<String[]>();
        int Length_ProcStat = 10;
        String tempString = "";
        boolean bIsProcInfo = false;
        String[] rows = null;
        String[] columns = null;
        rows = info.split("[\n]+");
        // 使用正则表达式分割字符串
        for (int i = 0; i < rows.length; i++) {
            tempString = rows[i];
            // AbLogUtil.d(AbAppUtil.class, tempString);
            if (tempString.indexOf("PID") == -1) {
                if (bIsProcInfo == true) {
                    tempString = tempString.trim();
                    columns = tempString.split("[ ]+");
                    if (columns.length == Length_ProcStat) {
                        // 把/system/bin/的去掉
                        if (columns[9].startsWith("/system/bin/")) {
                            continue;
                        }
                        // AbLogUtil.d(AbAppUtil.class,
                        // "#"+columns[9]+",PID:"+columns[0]);
                        processList.add(columns);
                    }
                }
            } else {
                bIsProcInfo = true;
            }
        }
        return processList;
    }

    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        // DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5,
        // xdpi=160.421, ydpi=159.497}
        // DisplayMetrics{density=2.0, width=720, height=1280,
        // scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    /**
     * 描述：kill进程.
     *
     * @param context
     * @param pid
     */
    public static void killProcesses(Context context, int pid,
                                     String processName) {

        String cmd = "kill -9 " + pid;
        String Command = "am force-stop " + processName + "\n";
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            os.writeBytes(Command + "\n");
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            sh.waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // AbLogUtil.d(AbAppUtil.class, "#kill -9 "+pid);
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = null;
        try {
            if (processName.indexOf(":") == -1) {
                packageName = processName;
            } else {
                packageName = processName.split(":")[0];
            }

            activityManager.killBackgroundProcesses(packageName);

            //
            Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static List<AppProcessInfo> mProcessLists = null;


    /**
     * 描述：top -n 1.
     *
     * @return
     */
    public static String runCommandTopN1() {
        try {
            final Process m_process = Runtime.getRuntime().exec("/system/bin/top -n 1");

            final StringBuilder sbread = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process.getInputStream()), 8192);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sbread.append(line + " " + "\n");
            }

            Log.d(TAG, "runCommandTopN1: " + sbread.toString());
            return sbread.toString();
        } catch (Exception e) {
            Log.d(TAG, "runCommandTopN1: ");

        }

        return "";
    }

    /**
     * 描述：根据进程名返回应用程序.
     *
     * @param context
     * @param processName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context,
                                                     String processName) {
        if (processName == null) {
            return null;
        }

        PackageManager packageManager = context.getApplicationContext()
                .getPackageManager();
        List<ApplicationInfo> appList = packageManager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }

    public static float getProcessCpuRate() {

        float totalCpu = getTotalCpuTime();
        float idleCpu = mIdleCpu;

        try {
            Thread.sleep(1000);

        } catch (Exception e) {
        }

        float totalCpu2 = getTotalCpuTime();
        float idleCpu2 = mIdleCpu;

//        float v = totalCpu2 - totalCpu;
//        float v1 = idleCpu2 - idleCpu;
//        if (totalCpu2 - totalCpu > 0) {
//            float cpuRate = 100 * (((totalCpu - totalCpu2) - (idleCpu - idleCpu2)) / (totalCpu - totalCpu2));
        float cpuRate = (((totalCpu2 - totalCpu) - (idleCpu2 - idleCpu)) / (totalCpu2 - totalCpu));

        if(cpuRate > 0.99f){
            cpuRate = 0.99f;
        }
        if(cpuRate < 0.04f){
            cpuRate = 0.05f;
        }
        return cpuRate;
    }

    public static float getTotalCpuTime() { // 获取系统总CPU使用时间
        String[] cpuInfos = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            Log.d(TAG, "getTotalCpuTime: load" + load);
            reader.close();
            cpuInfos = load.split(" ");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        float totalCpu = Float.parseFloat(cpuInfos[2])
                + Float.parseFloat(cpuInfos[3]) + Float.parseFloat(cpuInfos[4])
                + Float.parseFloat(cpuInfos[6]) + Float.parseFloat(cpuInfos[5])
                + Float.parseFloat(cpuInfos[7]) + Float.parseFloat(cpuInfos[8]);
        mIdleCpu = Float.parseFloat(cpuInfos[5]);
        return totalCpu;
    }

    /**
     * 描述：获取可用内存.
     */
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        // 当前系统可用内存 ,将获得的内存大小规格化
        return memoryInfo.availMem;
    }


    /**
     * 描述：总内存.
     */
    public static long getTotalMemory() {
        // 系统内存信息文件
        String file = "/proc/meminfo";
        String memInfo;
        String[] strs;
        long memory = 0;
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            // 读取meminfo第一行，系统内存大小
            memInfo = bufferedReader.readLine();
            strs = memInfo.split("\\s+");
            // 获得系统总内存，单位KB
            memory = Integer.valueOf(strs[1]).intValue();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Byte转位KB或MB
        return memory * 1024;
    }


    public static float getPercent(long memory) {
        long y = getTotalMemory();
        final double x = ((memory / (double) y) * 100);
        return new BigDecimal(x).setScale(2, BigDecimal.ROUND_HALF_UP)
                .floatValue();
    }

    public static float getPercent(Context context) {
        long l = getAvailMemory(context);
        long y = getTotalMemory();
        return getPercent(y - l);
    }


    public static float getFahrenheitToCentigrade(float temperature) {
        return (float) (Math.round(((temperature - 32) * 5 / 9) * 10)) / 10;
    }

    public static float getCentigradeToFahrenheit(float temperature) {
        return (float) (Math.round( (temperature * 9 / 5 + 32) * 10)) / 10;
    }
    public static final String PACKAGE_GP= "com.android.vending";
    public static final String CLASS_GP = "com.android.vending.AssetBrowserActivity";
    /**
     * 判断程序是否安装
     */
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            @SuppressWarnings("unused")
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void startGpMarket(Context context,String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setClassName(PACKAGE_GP, CLASS_GP);
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);
    }

    public static List<AppProcessInfo> getRunningProcessInfo(Context context, ScanApps scanCallback) {
        PackageManager packageManager = context.getPackageManager();
        List<AppProcessInfo> list = new ArrayList<>();
        Log.d(TAG, "doInBackground: ");
        ApplicationInfo appInfo = null;
        String result = AppUtils.runCommandTopN1();

        List<String[]> processList = AppUtils.parseProcessRunningInfo(result);

        String tempPidString = "";
        int count = processList.size();
        for (int i = 0; i < count; i++) {
            String[] item = processList.get(i);
            tempPidString = item[0];
            if (tempPidString == null) {
                continue;
            }
            // UID
            String uid = item[8];
            if (uid.equals("root") ||/* uid.equals("radio") ||
                    uid.equals("system") ||*/ uid.equals("shell")) {
                continue;
            }

            String processName = item[9];
            try {
                appInfo = packageManager.getApplicationInfo(processName, 0);
                if (appInfo == null) {
                    continue;
                }
            } catch (PackageManager.NameNotFoundException e) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).uid.equals(item[8]) || list.get(j).processName.equals(item[9])) {
                        if ((item[2].replace("%", "").equals("0"))) {
                            list.get(j).cpu += 2;
                            Log.d(TAG, "doInBackground: list.get(j      ).cpu" + list.get(j).cpu);
                        } else {
                            list.get(j).cpu = list.get(j).cpu + Integer.valueOf(item[2].replace("%", ""));
                            Log.d(TAG, "doInBackground: list.get(j).cpu" + list.get(j).cpu);

                        }
                        Log.d(TAG, "doInBackground: list.get(j).cpu" + list.get(j).cpu + "name" + list.get(j).appName);
                        break;
                    }
                }

                continue;
            }

            if (appInfo.processName.contains("com.abclauncher.") ||
                    appInfo.processName.equals(context.getPackageName())) {
                continue;
            }

            AppProcessInfo process = new AppProcessInfo();

            // Process Name
            process.processName = item[9];

            // Process ID
            process.pid = Integer.parseInt(item[0]);
            // CPU
            process.cpu = Integer.valueOf(item[2].replace("%", ""));

            // S
            process.status = item[3];
            // thread
            process.threadsCount = item[4];
            // Mem
            long mem = 0;
            if (item[5].indexOf("M") != -1) {
                mem = Long.parseLong(item[6].replace("M", "")) * 1000 * 1024;
            } else if (item[5].indexOf("K") != -1) {
                mem = Long.parseLong(item[6].replace("K", "")) * 1000;
            } else if (item[5].indexOf("G") != -1) {
                mem = Long.parseLong(item[6].replace("G", "")) * 1000 * 1024 * 1024;
            }
            process.memory = mem;

            // UID
            process.uid = item[8];


            Drawable icon = appInfo.loadIcon(packageManager);
            String appName = appInfo.loadLabel(packageManager)
                    .toString();
            process.icon = icon;
            process.appName = appName;
            process.isSystem = (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
            if(scanCallback != null){
                scanCallback.scanApp(process);
            }
            list.add(process);
        }
        return list;
    }

    public interface ScanApps{
        public void scanApp(AppProcessInfo processInfo);
    }
}
