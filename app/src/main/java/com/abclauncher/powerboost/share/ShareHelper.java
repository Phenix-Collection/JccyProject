package com.abclauncher.powerboost.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by shenjinliang on 16/11/9.
 */

public class ShareHelper {

    private final static String BASE_GP_URL = "https://play.google.com/store/apps/details?id=";
    private final static String DEFAULT_SHARE_URL = "https://batterysaver.app.link/s9WihFoDTz";

    /**
     * 得到支持分享的应用
     *
     * @return 返回支持分享的app集合
     */
    private static List<ShareItem> scanShareApp(Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, " ");

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> mResolveInfos = packageManager.queryIntentActivities(shareIntent, 0);

        ArrayList<ShareItem> shareItems = new ArrayList<>();
        for (ResolveInfo resolveInfo : mResolveInfos) {
            ShareItem shareItem = new ShareItem(resolveInfo.loadLabel(packageManager), resolveInfo.loadIcon(packageManager), resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
            Log.d(TAG, "scanShreaApp: getAppName" + shareItem.getAppName() + "resolveInfo" + resolveInfo.activityInfo.packageName + "name" + resolveInfo.activityInfo.name);

            //添加进入集合
            shareItems.add(shareItem);
        }
        return shareItems;
    }


    public static List<ShareItem> getSortShareItems(Context context) {
        List<ShareItem> shareItems = scanShareApp(context);
        List<String> priorityList = getPriorityList();
        Collections.sort(shareItems, new ItemShareComparator(priorityList));
        return shareItems;
    }

    private static ArrayList<String> getPriorityList() {
        ArrayList<String> packNames = new ArrayList<>();
        packNames.add("com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias");//facebook
        packNames.add("com.twitter.android.composer.ComposerActivity");//twitter
        packNames.add("com.facebook.messenger.intents.ShareIntentHandler");//messenger
        packNames.add("com.whatsapp.ContactPicker");//whatsapp
        return packNames;
    }

    public static void share(Context context, ComponentName chosenName){

        String shareUrl;
        if( chosenName.getPackageName().contains("facebook") &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT ){
            shareUrl = BASE_GP_URL + context.getPackageName();
        } else {
            shareUrl = DEFAULT_SHARE_URL;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setComponent(chosenName);
        intent.putExtra(Intent.EXTRA_TEXT, shareUrl);

        try {
            context.startActivity(intent);
        } catch (Exception e){
            Log.d(TAG, "shareFaceBookUnderKITKAT: " + e.getMessage());
        }
    }


    static class ItemShareComparator implements Comparator<ShareItem> {
        private List<String> mPriorityList;
        public ItemShareComparator(List<String> priorityList){
            mPriorityList = priorityList;
        }

        @Override
        public int compare(ShareItem lhs, ShareItem rhs) {
            if (mPriorityList.contains(lhs.getClassName()) && mPriorityList.contains(rhs.getClassName())) {
                if (mPriorityList.indexOf(lhs.getClassName()) < mPriorityList.indexOf(rhs.getClassName())) {
                    return mPriorityList.indexOf(lhs.getClassName()) - 1;
                } else {
                    return mPriorityList.indexOf(lhs.getClassName())
                            + 1;
                }
            }
            if (mPriorityList.contains(lhs.getClassName())) {
                return -mPriorityList.indexOf(lhs.getClassName()) - 1;
            }
            if (mPriorityList.contains(rhs.getClassName())) {
                return mPriorityList.indexOf(rhs.getClassName()) + 1;
            }
            return 0;
        }
    }
}
