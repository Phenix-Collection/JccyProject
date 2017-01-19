package com.abclauncher.powerboost.share;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 *  jin
 */
public class ShareItem implements Serializable {

    private CharSequence appName;
    private Drawable appIcon;
    private String className;
    private String packageName;

    ShareItem(CharSequence appName, Drawable appIcon, String packageName, String className) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.packageName = packageName;
        this.className =className;
    }


    public CharSequence getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public String getClassName() {
        return className;
    }
}
