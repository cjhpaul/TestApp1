package com.example.jeehwancho.testapp1;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by jeehwancho on 6/24/15.
 */
public class HitMeModel {
    private Bitmap m_bitmap;
    private int m_X, m_Y;

    public HitMeModel(Bitmap bitmap) {
        m_bitmap = bitmap;
        m_X = 0;
        m_Y = 0;
    }

    public void setX(int x) {
        m_X = x;
    }

    public void setY(int y) {
        m_Y = y;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(m_bitmap, m_X, m_Y, null);
    }

    public void update() {
        //do update
    }
}
