package com.abclauncher.powerboost.daemon;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by xq on 2016/9/27.
 * 5.0以下系统用此方案
 */
public class NdkProcess {

    private static NdkProcess mInstance;

    private final String FLAG_FILE_SERVICE;
    private final String PACKAGE_NAME;
    private RandomAccessFile mServiceFlagFile;

    private NdkProcess(Context context) {
        PACKAGE_NAME = context.getPackageName();
        FLAG_FILE_SERVICE = "/data/data/" + PACKAGE_NAME + "/apk.pid";
    }

    public static void start(Context context) {
        if (mInstance == null) {
            mInstance = new NdkProcess(context);
        }
        mInstance.startProcess();
    }

    public static void stop() {
        if (mInstance != null) {
            mInstance.stopProcess();
            mInstance = null;
        }
    }

    public void startProcess() {
        writeInt(android.os.Process.myPid());
        RunExecutable(PACKAGE_NAME, "libdaemon.so", "my_daemon", PACKAGE_NAME);
        Log.d(getClass().getSimpleName(), "start");
    }

    public void stopProcess() {
        writeInt(0);
        try {
            if(mServiceFlagFile != null) {
                mServiceFlagFile.close();
            }
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }

    private int getLittleEndianValue(int val) {
        return (val >>> 24) | (val << 24) | ((val << 8) & 0x00FF0000) | ((val >> 8) & 0x0000FF00);
    }

    private void writeInt(int val) {
        Log.d(getClass().getSimpleName(), "pid:" + val);
        try {
            if (mServiceFlagFile == null) {
                mServiceFlagFile = new RandomAccessFile(FLAG_FILE_SERVICE, "rws");
            }
            mServiceFlagFile.writeInt(getLittleEndianValue(val));
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.toString());
        }
    }

    private void RunExecutable(String pkgName, String filename, String alias, String args) {
        String path = "/data/data/" + pkgName;
        String srcFile = path + "/lib/" + filename;
        String distFile = path + "/" + alias;
        String runCmd = distFile + " " + args;
        String chmodCmd = "chmod 777 " + distFile;
        String copyFileCmd = "dd if=" + srcFile + " of=" + distFile;

        if (!new File(distFile).exists()) {
            RunLocalUserCommand(pkgName, copyFileCmd); // 拷贝lib/filename到上一层目录,同时命名为alias.
        }
        RunLocalUserCommand(pkgName, chmodCmd); // 改变test的属性,让其变为可执行
        RunLocalUserCommand(pkgName, runCmd); // 执行test程序.
    }

    /****************************************************
     * RunLocalUserCommand<br>
     * 执行本地用户命令
     **************************************************/
    private boolean RunLocalUserCommand(String pkgName, String command) {
        boolean result = false;
        Process process;
        try {
            process = Runtime.getRuntime().exec("sh"); // 获得shell进程
            DataInputStream inputStream = new DataInputStream(process.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("cd /data/data/" + pkgName + "\n"); // 保证在command在自己的数据目录里执行,才有权限写文件到当前目录

            outputStream.writeBytes(command + " &\n"); // 让程序在后台运行，前台马上返回
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            process.waitFor();

            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            Log.d(getClass().getSimpleName(), command + new String(buffer));
            result = true;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage(), e);
        }
        return result;
    }
}