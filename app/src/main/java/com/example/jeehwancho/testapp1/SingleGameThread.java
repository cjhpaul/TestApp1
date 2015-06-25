package com.example.jeehwancho.testapp1;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by jeehwancho on 6/22/15.
 */
public class SingleGameThread extends Thread {
    private static final String TAG = SingleGameThread.class.getSimpleName();
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
        while (m_isRunning) {
            canvas = null;
            try {
                canvas = m_surfaceHolder.lockCanvas();
                synchronized (m_surfaceHolder) {
                    m_singleGamePanel.update();
                    m_singleGamePanel.render(canvas);
                }
            } finally {
                if (canvas != null)
                    m_surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
        Log.d(TAG, "Run has ended.");
    }
}
