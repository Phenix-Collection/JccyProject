package com.abclauncher.powerboost.util;

import com.facebook.ads.Ad;

/**
 * Created by sks on 2016/10/8.
 */
public interface FacebookAdCallbackDtail extends FacebookAdCallback {
     void onNativeAdLoadError();
     void onNativeAdClick(Ad ad);
}
