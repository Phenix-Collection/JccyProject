package com.abclauncher.powerboost.util;

import android.content.Context;
import android.util.Log;

import com.abclauncher.powerboost.bean.FacebookNativeAdBean;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;

/**
 * Created by yoyo on 2016/4/13.
 */
public class AdUtil implements InterstitialAdListener {
    private static final String TAG = "AdUtilyy";
    public static final String AD_PLACEMENT_CLEAN_RESULT = "862014030568016_862015817234504";
    public static String AD_CHARGE_LOCKER = "862014030568016_862014880567931";
    public static String AD_CHARGE_UI = "862014030568016_862014507234635";
    private static AdUtil sFacebookAdUtil;

    public static AdUtil getInstance(){
        if (sFacebookAdUtil == null){
            sFacebookAdUtil = new AdUtil();
        }
        return sFacebookAdUtil;
    }

    public AdUtil(){}

    public static void loadNativeAd(final Context context, final String adPlacementId, final FacebookAdCallback callback){
        AdSettings.addTestDevice("dd3cd84dc2915359e4426d59b654b991");
        final NativeAd nativeAd = new NativeAd(context, adPlacementId);
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "ad view onCropError:" + adError.getErrorMessage());
                if(callback instanceof FacebookAdCallbackDtail){
                    ((FacebookAdCallbackDtail) callback).onNativeAdLoadError();
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "ad view onAdLoaded");
                if (ad != nativeAd) {
                    return;
                }

                String titleForAd = nativeAd.getAdTitle();
                NativeAd.Image coverImage = nativeAd.getAdCoverImage();
                NativeAd.Image iconForAd = nativeAd.getAdIcon();
                String actionForAd = nativeAd.getAdCallToAction();
                String subTitle = nativeAd.getAdSubtitle();
                String textForAdBody = nativeAd.getAdBody();
                FacebookNativeAdBean nativeAdBean = new FacebookNativeAdBean();

                if (titleForAd == null || coverImage == null || iconForAd == null) {
                    return;
                }

                nativeAdBean.title = titleForAd;
                nativeAdBean.coverImgUrl = coverImage.getUrl();
                nativeAdBean.iconForAdUrl = iconForAd.getUrl();
                nativeAdBean.textForAdBody = textForAdBody;
                nativeAdBean.actionBtnText = actionForAd;
                nativeAdBean.nativeAd = nativeAd;
                callback.onNativeAdLoaded(nativeAdBean);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "ad view onAdClicked");
                if(callback instanceof FacebookAdCallbackDtail){
                    ((FacebookAdCallbackDtail) callback).onNativeAdClick(ad);
                    loadNativeAd(context, adPlacementId,callback);
                }

            }
        });
        nativeAd.setImpressionListener(new ImpressionListener() {
            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "on ad impression");
            }
        });

        nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {
        Log.d(TAG, "onInterstitialDisplayed: ");
    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        Log.d(TAG, "onInterstitialDismissed: ");
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        Log.d(TAG, "onCropError: ");
    }

    @Override
    public void onAdLoaded(Ad ad) {
        Log.d(TAG, "onAdLoaded: ");
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.d(TAG, "onAdClicked: ");
    }
}
