package com.example.jeehwancho.testapp1;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jeehwancho on 6/24/15.
 */
public class HitMeModel {
    private Bitmap m_bitmap;
    private float m_X, m_Y;
    private long m_expirationTime;

    public HitMeModel(Bitmap bitmap, float x, float y) {
        m_bitmap = bitmap;
        m_X = x;
        m_Y = y;
        m_expirationTime = 0;
    }

    public void setXY(float x, float y) {
        m_X = x;
        m_Y = y;
    }

    public void draw(Canvas canvas, long curTimeMillis) {
        if (curTimeMillis < m_expirationTime) {
            canvas.drawBitmap(m_bitmap, m_X, m_Y, null);
        }
    }

    public void makeVisibleFor(long additionalMillis, long curTimeMillis) {
        m_expirationTime = curTimeMillis + additionalMillis;
    }
}
