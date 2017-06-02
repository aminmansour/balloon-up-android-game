package com.example.balloonpop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/**
 * Created by Amans on 30/05/2017.
 */

public class Slider implements Animator.AnimatorListener {


    private final String COLORSLIDE = "#ff0099cc";
    private LinearLayout top;
    private AppCompatActivity context;
    private ViewGroup contentView;
    private LinearLayout bottom;
    private ValueAnimator animator;
    private int screenHeight;
    private final int BUFFER = 200;

    public Slider(AppCompatActivity a, ViewGroup contentView, int screenHeight, int screenWidth){
        top = new LinearLayout(a);
        context = a;
        this.contentView = contentView;
        top.setLayoutParams(new ViewGroup.LayoutParams(screenWidth,screenHeight/2));
        top.setBackgroundColor(Color.parseColor(COLORSLIDE));
        top.setTranslationY(0);
        top.setId(R.id.top_slide);
        contentView.addView(top);
        bottom = new LinearLayout(a);
        bottom.setLayoutParams(new ViewGroup.LayoutParams(screenWidth,screenHeight/2));
        bottom.setBackgroundColor(Color.parseColor(COLORSLIDE));
        bottom.setTranslationY(screenHeight/2);
        bottom.setId(R.id.bottom_slide);
        contentView.addView(bottom);
        this.screenHeight = screenHeight;
    }

    public void play(){
        animator = new ValueAnimator();
        animator.setDuration(720);
        animator.setFloatValues(screenHeight+BUFFER, screenHeight/2);
        animator.setInterpolator(new LinearInterpolator());
        animator.setTarget(this);
        animator.addListener(this);
        animator.start();
    }


    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

            ValueAnimator fadeIn = new ValueAnimator();
            fadeIn.setDuration(520);
            fadeIn.setFloatValues(-10, 1);
            fadeIn.setInterpolator(new LinearInterpolator());
            fadeIn.addUpdateListener(new AnimatorUpdateListener()  {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    context.findViewById(R.id.logo).setAlpha(animation.getAnimatedFraction());
                }
            });
            fadeIn.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator fadeOut = new ValueAnimator();
                fadeOut.setStartDelay(1200);
                fadeOut.setDuration(520);
                fadeOut.setFloatValues(0, 1);
                fadeOut.setInterpolator(new LinearInterpolator());
                fadeOut.addUpdateListener(new AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        context.findViewById(R.id.logo).setAlpha(1-animation.getAnimatedFraction());
                    }
                });
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ValueAnimator slideOut = new ValueAnimator();
                        slideOut.setDuration(2320);
                        slideOut.setFloatValues(screenHeight/2, screenHeight);
                        slideOut.setInterpolator(new LinearInterpolator());
                        slideOut.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                contentView.removeView(top);
                                contentView.removeView(bottom);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        slideOut.addUpdateListener(new AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                bottom.setTranslationY((Float) animation.getAnimatedValue());
                                   if(top.getTranslationY()>-590)
                                    top.setTranslationY(-((float) animation.getAnimatedValue() - screenHeight / 2));
                                    }
                        });
                        contentView.removeView(context.findViewById(R.id.initial_screen));
                        slideOut.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                fadeOut.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        fadeIn.start();



    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }


}
