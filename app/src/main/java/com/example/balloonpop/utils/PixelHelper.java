package com.example.balloonpop.utils;

/**
 * Created by Amans on 19/05/2017.
 */

import android.content.Context;
import android.util.TypedValue;

public class PixelHelper {

    public static int pixelsToDp(int px, Context context) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, px,
                context.getResources().getDisplayMetrics());
    }

}
