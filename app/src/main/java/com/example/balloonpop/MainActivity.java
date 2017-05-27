package com.example.balloonpop;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.balloonpop.utils.HighScoreHelper;
import com.example.balloonpop.utils.MusicHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {



    //settings
    private final int MAX_DELAY = 2000;
    private final int MIN_DELAY = 1000;
    private final int MAX_DUR = 2000;
    private final int MIN_DUR = 10;
    private final int TOTAL_LIFES = 4;

    //fields
    private ViewGroup contentView;
    private TextView tvScore,tvLevel;
    private ImageView[] hearts;
    private MusicHelper music;
    private int[] balloonColors;
    private int nextBallonColor,screenWidth,screenHeight = 0;
    private int level = 0;
    private int life = TOTAL_LIFES;
    private int score = 0;
    private ArrayList<Balloon> ballonsOnScreen;
    private RelativeLayout rl;
    private boolean isPaused = false;
    private int levelDifficultyAcc;
    private boolean isGameOver = false;
    private boolean levelStarted = false;
    private BalloonLauncher bl;
    private Button b;

    private final Random r = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //background
        getWindow().setBackgroundDrawableResource(R.mipmap.city_background_4267x2133);

        //set it to full screen
        setFullscreen();
        setContentView();

        //gets dimension of screen
        ViewTreeObserver v = contentView.getViewTreeObserver();
        if (v.isAlive()){
            v.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    screenWidth = contentView.getWidth();
                    screenHeight = contentView.getHeight();
                }
            });
        }

        //sets up variables
        setUpGameEnvironment();

        //plays music
        music = new MusicHelper(this,this);
        music.playMusic();
    }

    private void setUpGameEnvironment(){
        level = 0;
        score = 0;
        levelDifficultyAcc = 200;
        life = TOTAL_LIFES;
        isPaused= false;
        isGameOver = false;
        levelStarted = false;
        balloonColors = new int[]{Color.parseColor("#00A8FF"), Color.parseColor("#FF2654"),
                Color.parseColor("#FAFF65"),Color.parseColor("#52FF72"),
                Color.parseColor("#FF63CC"),Color.parseColor("#783FFF"),Color.parseColor("#FFCA10")
        ,Color.parseColor("#00FFF0")};
        nextBallonColor = r.nextInt(balloonColors.length) ;
        tvScore = ((TextView)findViewById(R.id.score_input));
        tvLevel = ((TextView)findViewById(R.id.level_input));
        hearts = new ImageView[]{(ImageView)findViewById(R.id.heart1),(ImageView)findViewById(R.id.heart2),
                (ImageView)findViewById(R.id.heart3),(ImageView)findViewById(R.id.heart4)};
        updateScore();
        ballonsOnScreen = new ArrayList<Balloon>();
        rl = (RelativeLayout)findViewById(R.id.game_over);
        contentView.removeView(rl);
        for(int i = 0;i<TOTAL_LIFES;++i) {
            hearts[i].setColorFilter(Color.parseColor("#ff4444"));
        }
    }

    //retrieves xml content view
    private void setContentView() {
        contentView = (ViewGroup) findViewById(R.id.activity_main);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullscreen();
            }
        });
    }

    //sets full screen
    private void setFullscreen(){
        ViewGroup rootLayout = (ViewGroup)findViewById(R.id.activity_main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //activity actions
    @Override
    protected void onStop() {
        super.onStop();
        music.pauseMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        music.pauseMusic();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        music.playMusic();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setFullscreen();
    }


    //inner class which runs background thread releasing ballons
    private class BalloonLauncher extends AsyncTask<Integer,Integer,Void>{


        @Override
        protected Void doInBackground(Integer... params) {
            int level = params[0];
            int balloonsLaunched = 0;
            int xPos = 0;

            int maxDelay,minDelay = 0;
            while (!isGameOver) {
                if (!isPaused) {
                    if(!levelStarted) {
                        publishProgress(0, 1, 0);
                        balloonsLaunched = 0;
                        xPos = 0;
                        maxDelay = Math.max(MIN_DELAY, MAX_DELAY - (100 * level));
                        minDelay = maxDelay / 2;
                    }
                    while (!isPaused && balloonsLaunched < 4) {
                        levelStarted = true;
                        if (isGameOver) {
                            return null;
                        }
                        //pick x location of next balloon
                        xPos = r.nextInt(screenWidth - 200);
                        //icrements ballon launched
                        balloonsLaunched++;
                        //accesses ui thread and releases a ballon in the view
                        publishProgress(xPos, 0,balloonsLaunched);
                        try {
                            Thread.sleep(minDelay + r.nextInt(minDelay));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(2000);
                        //increase level
                        ++level;
                        if(levelDifficultyAcc>40) {
                            levelDifficultyAcc -= 15;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //sets level and updates score
            if(values[1]==1){
                level++;
                updateScore();
            }else {
                //launches ballon
                launchBalloon(values[0]);
                if(values[2]==4){
                    levelStarted = false;
                }
            }
        }
    }



    private void launchBalloon(int x){
        Balloon b = new Balloon(MainActivity.this,balloonColors[nextBallonColor],100);
        ballonsOnScreen.add(b);
        nextBallonColor = (nextBallonColor+1)%balloonColors.length;
        b.setX(x);
        b.setY(screenHeight);
        b.releaseBalloon(screenHeight,Math.max(MIN_DUR,MAX_DUR-(levelDifficultyAcc*level)));
        contentView.addView(b);
        findViewById(R.id.toolbar).bringToFront();
    }

    public void startHandler(View view){
        bl = new BalloonLauncher();
        bl.execute(level);
        final Button buttonClicked = (Button)view;
        buttonClicked.setText("Pause");
        buttonClicked.setOnClickListener(null);
        buttonClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused=!isPaused;
                pauseAlternation(isPaused);
                if(isPaused){
                    buttonClicked.setText("Continue");
                }else{
                    buttonClicked.setText("Pause");
                }
            }
        });
    }


    private void updateScore(){
        tvScore.setText(""+score);
        tvLevel.setText(""+level);
    }

    public void popBalloon(Balloon balloon,boolean clicked){
        contentView.removeView(balloon);
        ballonsOnScreen.remove(balloon);
        if(clicked){
            //increase score and play sound effect
            score++;
            music.playBallonPop();
        }else {
            //decrement life and set color of a heart to grey
            life--;
            hearts[life].setColorFilter(Color.parseColor("#B8B8B8"));
            //if life is now zero, end game
            if(life==0){gameOver(true);}
        }
            updateScore();
    }

    private void gameOver(boolean isOver) {
        if(isOver){

            //stop float animation and remove from screen
            for (Balloon b:ballonsOnScreen) {
                contentView.removeView(b);
                b.stopAnimation();
            }

            isGameOver = true;
            ballonsOnScreen.clear();
            b = (Button)findViewById(R.id.go_button);
            b.setClickable(false);

            //add game over screen to view
            contentView.addView(rl);
            handleHighScore();

            //restart button
            ((Button)findViewById(R.id.play_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setUpGameEnvironment();
                    bl = new BalloonLauncher();
                    bl.execute(level);
                    b.setClickable(true);
                }
            });
        }
    }

    private void handleHighScore() {
        ((TextView)findViewById(R.id.current_score)).setText("Score   :   "+score);
        //gets current high score
        int highScore = HighScoreHelper.getTopScore(getApplicationContext());
        TextView tvHighScore = (TextView)findViewById(R.id.high_score);
        if(highScore<score){
            //set new high score
            HighScoreHelper.setTopScore(getApplicationContext(),score);
            tvHighScore.setText("New High Score   :   "+score);
        }else{
            ((TextView)findViewById(R.id.high_score)).setText("High Score   :   "+highScore);
        }
    }

    public void pauseAlternation(boolean toPause){
        for (Balloon b :ballonsOnScreen) {
            //pause balloon
            b.pauseAnimation(toPause);
        }
    }




}
