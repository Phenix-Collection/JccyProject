package com.abclauncher.powerboost.mode.util;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by sks on 2016/12/27.
 */

public class AudioUtil {
    private static AudioUtil sAudioUtil;
    private Context mContext;
    private AudioManager mAudioManager;

    public static AudioUtil getInstance(Context context) {
        if (sAudioUtil == null){
            sAudioUtil = new AudioUtil(context);
        }
        return sAudioUtil;
    }

    private AudioUtil(Context context){
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setRingerVolume(int percent){
        int maxVolume = getMaxRingVolume();
        int volume = (int) (maxVolume * percent * 1.0f / 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    public int getMaxRingVolume(){
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    public int getCurrentRingVolume(){
        return mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
    }

    public int getPercentRingVolume(){
        return (int) (getCurrentRingVolume() * 100.0f / getMaxRingVolume());
    }

    public void setMediaVolume(int percent){
        int maxVolume = getMaxMediaVolume();
        int volume = (int) (maxVolume * percent * 1.0f / 100);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_PLAY_SOUND);
    }

    public int getMaxMediaVolume(){
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getCurrentMediaVolume(){
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public int getPercentMediaVolume(){
        return (int) (getCurrentMediaVolume() * 100.0f / getMaxMediaVolume());
    }

    public void setRingMode(int mode) {
        mAudioManager.setRingerMode(mode);
    }
}
