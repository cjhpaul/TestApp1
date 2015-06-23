package com.example.jeehwancho.testapp1;

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

    public void SetRunning(boolean isRunning) {
        this.m_isRunning = isRunning;
    }

    @Override
    public void run() {
        long tickCount = 0L;
        Log.d(TAG, "Run has begun.");
        while (m_isRunning) {
            tickCount++;
        }
        Log.d(TAG, "Run has ended. " + tickCount + " times.");
    }
}
