package com.abclauncher.powerboost.util;


import android.content.Context;

import com.abclauncher.powerboost.bean.FacebookNativeAdBean;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;

public class AdHelper {
    private AdHelper() {
    }
    public static class AdIds{
         public static final String AD_SIMPLE_LOCKER = "862014030568016_862014880567931";
         public static final String AD_RESULT = "1692374034408501_1693874930925078";
    }
    public static void showAd(final Context context, final String adID, final FbAdCallback fbAdListener) {
        final NativeAd nativeAd = new NativeAd(context, adID);
        AdSettings.addTestDevice("a3fd51fa69594b7d220ecbcdc6c96881");
        nativeAd.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                if (fbAdListener != null) {
                    fbAdListener.onError(ad, adError);
                }
            }
            @Override
            public void onAdLoaded(Ad ad) {
                if (fbAdListener != null) {
                    if (ad != nativeAd) {
                        return;
                    }
                    String titleForAd = nativeAd.getAdTitle();
                    NativeAd.Image coverImage = nativeAd.getAdCoverImage();
                    NativeAd.Image iconForAd = nativeAd.getAdIcon();
                    String actionForAd = nativeAd.getAdCallToAction();
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
                    fbAdListener.onNativeAdLoaded(nativeAdBean);
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (fbAdListener != null) {
                    fbAdListener.onAdClicked(ad);
                    showAd(context, adID,fbAdListener);
                }
            }
        });
        nativeAd.loadAd();
    }
    public interface FbAdCallback {
        void onError(Ad ad, AdError adError);

        void onNativeAdLoaded(FacebookNativeAdBean facebookNativeAdBean);

        void onAdClicked(Ad ad);
    }

}
