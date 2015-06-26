package com.example.jeehwancho.testapp1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeehwancho on 6/22/15.
 */
public class SingleGamePanel extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = SingleGamePanel.class.getSimpleName();
    private SingleGameThread m_singleGameThread;
    private HitMeModel m_hitMeModel;
    private float m_h, m_w;
    private List<HitMeModel> m_models;
    private Bitmap m_bitmapPikachu;

    public SingleGamePanel(Context context) {
        super(context);
        getHolder().addCallback(this);
        m_singleGameThread = new SingleGameThread(getHolder(), this);
        setFocusable(true);
        m_models = new ArrayList();

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        m_h = metrics.heightPixels / 10;
        m_w = metrics.widthPixels / 4;
        m_bitmapPikachu = BitmapFactory.decodeResource(getResources(), R.drawable.pikachu);
        m_bitmapPikachu = Bitmap.createScaledBitmap(m_bitmapPikachu, (int) m_w, (int) m_h * 2, true);
        m_hitMeModel = new HitMeModel(m_bitmapPikachu, 0, 0);

        //generate 4by4 models
        for (int i = 1; i < 9; i += 2)
            for (int j = 0; j < 4; j++)
                m_models.add(new HitMeModel(m_bitmapPikachu, m_w * j, m_h * i));
        Assert.assertEquals(16, m_models.size());
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
                m_hitMeModel.setXY(event.getX(), event.getY());
                Log.d(TAG, "x=" + event.getX() + ",y=" + event.getY());
            }
        }
        return super.onTouchEvent(event);
    }

    public void update() {
        m_hitMeModel.update(true);
        for (HitMeModel model : m_models)
            model.update(true);
    }

    public void render(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        m_hitMeModel.draw(canvas);
        for (HitMeModel model : m_models)
            model.draw(canvas);
    }
}
