package com.example.balloonpop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.balloonpop.utils.PixelHelper;

/**
 * Created by Amans on 19/05/2017.
 */

public class Balloon extends AppCompatImageView implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private boolean isPoped;
    private ValueAnimator animator;
    private MainActivity c;

    public Balloon(Context context) {
        super(context);
    }

    public Balloon(Context context,int color,int rawHeight){
        super(context);
        c = (MainActivity)context;
        this.setImageResource(R.mipmap.balloon);
        this.setColorFilter(color);
        int rawWidth = rawHeight/2;
        int dpHeight = PixelHelper.pixelsToDp(rawHeight,context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth,context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth,dpHeight);
        setLayoutParams(params);
    }

    public void releaseBalloon(int screenHeight,int duration){
        animator = new ValueAnimator();
        animator.setDuration(duration);
        animator.setFloatValues(screenHeight, -130f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setTarget(this);
        animator.addListener(this);
        animator.addUpdateListener(this);
        animator.start();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
       if(!isPoped) {
           c.popBalloon(this, false);
       }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //when float value changes update y of ballon
        setTranslationY((float)animation.getAnimatedValue());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isPoped&&!animator.isPaused()&&event.getAction()==MotionEvent.ACTION_DOWN){
            stopAnimation();
            c.popBalloon(this, true);

        }
        return super.onTouchEvent(event);
    }

    public void stopAnimation() {
        isPoped = true;
        //cancels float animation
        animator.cancel();
        //pops ballon
    }


    public void pauseAnimation(boolean toPause){
        if(toPause){
            animator.pause();
        }else{
            animator.resume();
        }
    }
}
