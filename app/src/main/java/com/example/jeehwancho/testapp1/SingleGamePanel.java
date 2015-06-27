package com.example.jeehwancho.testapp1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jeehwancho on 6/22/15.
 * Reward:
 * (1) user clicks a right model.
 *
 * Penalty:
 * (1) user clicks a wrong model.
 * (2) model went off while not getting hit
 */
public class SingleGamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = SingleGamePanel.class.getSimpleName();
    private SingleGameThread m_singleGameThread;
    private float m_h, m_w;
    private HitMeModel m_cursorModel;
    private List<HitMeModel> m_models;
    private Bitmap m_bitmapPikachu;

    private Random m_die;
    private int m_maxModelNum;
    private float[] m_Xs;
    private float[] m_Ys;
    private Paint m_scorePaint;
    private long m_score;

    //time variables
    private long m_curTimeMillis;
    private long m_lastTimeUpdated;
    private long m_startTime;
    private long m_timeLeft;

    //difficulty variables
    private long m_difficultyUpdateInterval;
    private long m_difficultyVisibilityInterval;
    private long m_difficultyNumObjects;

    public SingleGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        m_singleGameThread = new SingleGameThread(getHolder(), this);
        setFocusable(true);

        m_score = 0;
        m_die = new Random();
        m_models = new ArrayList();

        //get screen size
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        m_h = metrics.heightPixels / 10;
        m_w = metrics.widthPixels / 4;

        //init painter for texts
        m_scorePaint = new Paint();
        m_scorePaint.setColor(Color.WHITE);
        m_scorePaint.setStyle(Paint.Style.FILL);
        m_scorePaint.setTextSize(m_h / 3);

        //init images
        m_bitmapPikachu = BitmapFactory.decodeResource(getResources(), R.drawable.pikachu);
        m_bitmapPikachu = Bitmap.createScaledBitmap(m_bitmapPikachu, (int) m_w, (int) m_h * 2, true);
        m_cursorModel = new HitMeModel(m_bitmapPikachu, 0, 0);

        //generate 4by4 models
        m_Xs = new float[4];
        m_Ys = new float[4];
        int index_i = 0;
        for (int i = 1; i < 9; i += 2) { //i corresponds to Y
            m_Ys[index_i++] = m_h * i;
            for (int j = 0; j < 4; j++) { //j corresponds to X
                m_models.add(new HitMeModel(m_bitmapPikachu, m_w * j, m_h * i));
                m_Xs[j] = m_w * j;
            }
        }
        Assert.assertEquals(16, m_models.size());
        m_maxModelNum = m_models.size();

        //init difficulties
        m_difficultyNumObjects = 1;
        m_difficultyUpdateInterval = 1000;
        m_difficultyVisibilityInterval = 1000;

        //init time variables - this has to go last
        m_curTimeMillis = System.currentTimeMillis();
        m_lastTimeUpdated = m_curTimeMillis;
        m_startTime = m_curTimeMillis;
        m_timeLeft = 60000;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_singleGameThread.setRunning(true);
        m_singleGameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed has started.");
        boolean retry = true;
        while (retry) {
            try {
                m_singleGameThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        Log.d(TAG, "surfaceDestroyed has ended.");
    }

    /**
     * When it is called: when user touches screen
     * What it does:
     * (1) detects touch gestures
     * (2) gives reward or penalty
     * (3) exits back to main activity
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > getHeight() - 50) {
                m_singleGameThread.setRunning(false);
                ((Activity) getContext()).finish();
            } else {
                m_cursorModel.makeVisibleFor(100, m_curTimeMillis);
                m_cursorModel.setXY(event.getX(), event.getY());

                int index = getIndexOfModelFromXY(event.getX(), event.getY());
                if (m_models.get(index).hit(m_curTimeMillis))
                    updateScoreAndDifficulties(1, 0);
                else //wrong model, penalty
                    updateScoreAndDifficulties(-1, 1000);
                Log.d(TAG, "x=" + event.getX() + ",y=" + event.getY() + ", idx=" + String.valueOf(index));
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * When it is called: every cycle. For that reason, it needs to be really efficient.
     * What it does:
     * (1) controls difficulty of game: update interval, visibility interval, # models
     * (2) keeps track of timer
     * (3) spawns random models by giving them visibility time
     * (4) detects time-out models and gives respective penalties
     */
    public void update() {
        //this should be the only place to update current time
        m_curTimeMillis = System.currentTimeMillis();
        m_timeLeft = 60000 - m_curTimeMillis + m_startTime;
        if (m_timeLeft < 0) {
            m_singleGameThread.setRunning(false);
            ((Activity) getContext()).finish();
        }
        //if update interval (in millis) has passed, do the actual update
        if (hasLastUpdatedElapsedFor(m_difficultyUpdateInterval)) {
            for (HitMeModel model : m_models) {
                if (model.checkIfTimeOut(m_curTimeMillis)) {
                    updateScoreAndDifficulties(-1, 1000);
                }
            }
            //randomly pick models, and make them visible
            for (long i = 0; i < m_difficultyNumObjects; i++) {
                int randomIndex = m_die.nextInt(m_maxModelNum);
                Log.d(TAG, "idx=" + String.valueOf(randomIndex));
                m_models.get(randomIndex).makeVisibleFor(m_difficultyVisibilityInterval, m_curTimeMillis);
            }
        }
    }

    /**
     * When it is called: every cycle. For that reason, it needs to be really efficient.
     * What it does:
     * (1) draw whatever appears in screen.
     */
    public void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        canvas.drawText("Score: " + String.valueOf(m_score) + ", Time: " + String.valueOf(m_timeLeft),
                m_w / 2, m_h / 2, m_scorePaint);
        m_cursorModel.draw(canvas, m_curTimeMillis);
        for (HitMeModel model : m_models)
            model.draw(canvas, m_curTimeMillis);
    }

    private boolean hasLastUpdatedElapsedFor(long updateIntervalInMillis) {
        boolean rtn = m_curTimeMillis > m_lastTimeUpdated + updateIntervalInMillis;
        if (rtn) { //if time has sufficiently passed, update lastTimeUpdated as well.
            m_lastTimeUpdated = m_curTimeMillis;
        }
        return rtn;
    }

    private int getIndexOfModelFromXY(float x, float y) {
        int xIndex = 3; //by default
        int yIndex = -1; //by default
        for (int i = 0; i < 3; i++) {
            if (m_Xs[i] < x && x < m_Xs[i + 1])
                xIndex = i;
        }
        for (int i = 0; i < 3; i++) {
            if (m_Ys[i] < y && y < m_Ys[i + 1])
                yIndex = i;
        }
        if (yIndex < 0 && y < m_h * 9) {
            yIndex = 3;
        }

        if (yIndex < 0)
            return -1;
        return yIndex * 4 + xIndex;
    }

    public long getScore() {
        return m_score;
    }

    //m_difficultyUpdateInterval - m_score
    //m_difficultyVisibilityInterval - m_score
    //m_difficultyNumObjects incremented every 16-score
    private void updateScoreAndDifficulties(long scoreIncrement, long timeDecrement) {
        m_score = m_score + scoreIncrement;
        if (m_score < 0)
            m_score = 0;
        m_difficultyUpdateInterval = 1000 - m_score;
        m_difficultyVisibilityInterval = 1000 - m_score;
        m_difficultyNumObjects = (m_score >> 4) + 1;
        m_startTime -= timeDecrement;
    }
}
