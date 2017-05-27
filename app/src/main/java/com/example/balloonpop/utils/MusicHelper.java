package com.example.balloonpop.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;

import com.example.balloonpop.R;

/**
 * Created by Amans on 21/05/2017.
 * Class for music and any sound effects
 */

public class MusicHelper {
    private final MediaPlayer media;
    private SoundPool mSoundPool;
    private int mSoundID;
    private boolean mLoaded;
    private float mVolume;


    public MusicHelper(Activity activity,Context c){
        media = MediaPlayer.create(c.getApplicationContext(), R.raw.city_sound);
        media.setVolume(0.5f,0.5f);
        media.setLooping(true);


        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;

        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder().setAudioAttributes(audioAttrib).setMaxStreams(6).build();
        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoaded = true;
            }
        });
        mSoundID = mSoundPool.load(activity, R.raw.balloon_pop1, 1);
    }

    public void playBallonPop() {
        if (mLoaded) {
            mSoundPool.play(mSoundID, 1f, 1f, 1, 0, 1f);
        }
    }

    public void playMusic(){
       if(media != null) {
           media.start();
       }
    }

    public void pauseMusic(){
        if(media!= null && media.isPlaying()){
            media.pause();
        }
    }



}
