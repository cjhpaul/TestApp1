package com.example.jeehwancho.testapp1;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jeehwancho on 6/22/15.
 */
public class SingleGamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = SingleGamePanel.class.getSimpleName();
    private SingleGameThread m_singleGameThread;
    private HitMeModel m_hitMeModel;

    public SingleGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        m_singleGameThread = new SingleGameThread(getHolder(), this);
        m_hitMeModel = new HitMeModel(BitmapFactory.decodeResource(getResources(), R.drawable.pikachu));
        setFocusable(true);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > getHeight() - 50) {
                m_singleGameThread.setRunning(false);
                ((Activity) getContext()).finish();
            } else {
                m_hitMeModel.setX((int) event.getX());
                m_hitMeModel.setY((int) event.getY());
                Log.d(TAG, "x=" + event.getX() + ",y=" + event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        //update status
        m_hitMeModel.update();
    }

    public void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        m_hitMeModel.draw(canvas);
    }
}
