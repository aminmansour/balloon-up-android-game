package com.example.balloonpop.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Amans on 21/05/2017.
 * Classes for storing and retrieving high score in shared resources
 */

public class HighScoreHelper {

    public static final String STORE_NAME = "bpstore";
    public static final String TOP_SCORE = "top_score";
    private static SharedPreferences getPreferences(Context c){
        return c.getSharedPreferences(STORE_NAME,0);
    }


    public static int getTopScore(Context c){
        return getPreferences(c).getInt(TOP_SCORE,0);
    }

    public static void setTopScore(Context c,int topScore){
        SharedPreferences.Editor editor = getPreferences(c).edit();
        editor.putInt(TOP_SCORE,topScore);
        editor.apply();
    }
}
