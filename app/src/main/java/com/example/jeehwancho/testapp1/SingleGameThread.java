package com.example.jeehwancho.testapp1;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by jeehwancho on 6/22/15.
 * Thread class that keeps max FPS
 */
public class SingleGameThread extends Thread {
    private static final String TAG = SingleGameThread.class.getSimpleName();
    private final static int MAX_FPS = 40; // desired fps
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD = 1000 / MAX_FPS;
    private SurfaceHolder m_surfaceHolder;
    private SingleGamePanel m_singleGamePanel;
    private boolean m_isRunning;

    public SingleGameThread(SurfaceHolder surfaceHolder, SingleGamePanel singleGamePanel) {
        super();
        this.m_surfaceHolder = surfaceHolder;
        this.m_singleGamePanel = singleGamePanel;
    }

    public void setRunning(boolean isRunning) {
        this.m_isRunning = isRunning;
    }

    @Override
    public void run() {
        Canvas canvas;
        Log.d(TAG, "Run has begun.");
        long beginTime;
        long timeDiff;
        int sleepTime;
        int framesSkipped;
        while (m_isRunning) {
            canvas = null;
            try {
                canvas = m_surfaceHolder.lockCanvas();
                synchronized (m_surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    m_singleGamePanel.update();
                    m_singleGamePanel.render(canvas);
                    timeDiff = System.currentTimeMillis() - beginTime;

                    sleepTime = (int) (FRAME_PERIOD - timeDiff);
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        this.m_singleGamePanel.update(); // update without rendering
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                }
            } finally {
                if (canvas != null)
                    m_surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        Log.d(TAG, "Run has ended.");
    }
}
