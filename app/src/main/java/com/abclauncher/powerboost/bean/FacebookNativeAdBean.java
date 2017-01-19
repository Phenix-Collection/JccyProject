package com.abclauncher.powerboost.bean;

import com.facebook.ads.NativeAd;


public class FacebookNativeAdBean {
    public String title;
    public String coverImgUrl;
    public String iconForAdUrl;
    public String textForAdBody;
    public String actionBtnText;
    public NativeAd nativeAd;

    @Override
    public String toString() {
        return "FacebookNativeAdBean:[title:" + title + " coverImgUrl:" + coverImgUrl + " iconForAdUrl:" + iconForAdUrl + " body:" + textForAdBody +"]";
    }

}
