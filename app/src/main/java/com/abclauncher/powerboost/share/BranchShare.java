package com.abclauncher.powerboost.share;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by shenjinliang on 16/11/17.
 */

public class BranchShare {
    private final static String TAG = "BranchShare";
    private final static String DEFAULT_IMAGE_URL = "http://pic.thanksearch.com/10/2016/1102/2f/2/253061/600x314x75x0x0x1.jpg";

    private BranchUniversalObject mBranchUniversalOj;

    public interface ShortUrlCallback{
        void onShortUrlCreate(String url);
        void onShortUrlCreateFailed(String errorMessage);
    }

    public BranchShare(String identifier, String title, String des, String imgUrl){
        mBranchUniversalOj = new BranchUniversalObject()
                .setCanonicalIdentifier(identifier)
                .setTitle(title)
                .setContentDescription(des)
                .setContentImageUrl(imgUrl)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC);
    }


    public void setDescriptionInfo(String identifier,String title, String description, String imageUrl){
        if(imageUrl == null){
            imageUrl = DEFAULT_IMAGE_URL;
        }
        mBranchUniversalOj.setCanonicalIdentifier(identifier)
                .setTitle(title)
                .setContentDescription(description)
                .setContentImageUrl(imageUrl);
    }


    public LinkProperties createLinkProperties(HashMap<String, String> linkContents){
        LinkProperties linkProperties = new LinkProperties()
                .setDuration(100);
        for(Map.Entry<String, String> entry : linkContents.entrySet()){
            switch (entry.getKey()){
                case "tag":
                    linkProperties.addTag(entry.getValue());
                    break;

                case "channel":
                    linkProperties.setChannel(entry.getValue());
                    break;

                case "feature":
                    linkProperties.setFeature(entry.getValue());
                    break;

                case "campaign":
                    linkProperties.setCampaign(entry.getValue());
                    break;
                default:
                    linkProperties.addControlParameter(entry.getKey(), entry.getValue());
                    break;
            }
        }

        return linkProperties;
    }


    public void generateShortUrl(Context context, LinkProperties linkProperties,
                                 final ShortUrlCallback callback){
        mBranchUniversalOj.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                if(error == null){
                    Log.d(TAG, "onLinkCreate: create short link " + url);
                    if(callback != null){
                        callback.onShortUrlCreate(url);
                    }
                } else {
                    Log.d(TAG, "onLinkCreate: create short link failed! " + error.getMessage());
                    callback.onShortUrlCreateFailed(error.getMessage());
                }
            }
        });
    }

}
