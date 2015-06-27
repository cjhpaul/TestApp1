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
    private ModelState m_state;
    public HitMeModel(Bitmap bitmap, float x, float y) {
        m_bitmap = bitmap;
        m_X = x;
        m_Y = y;
        m_expirationTime = 0;
        m_state = new ModelState();
    }

    public void setXY(float x, float y) {
        m_X = x;
        m_Y = y;
    }

    public void draw(Canvas canvas, long curTimeMillis) {
        //if animated, do animation
        //otherwise, you just draw stuff
        if (curTimeMillis < m_expirationTime) {
            canvas.drawBitmap(m_bitmap, m_X, m_Y, null);
        }
    }

    public void makeVisibleFor(long additionalMillis, long curTimeMillis) {
        m_state.actionMakeVisible();
        m_expirationTime = curTimeMillis + additionalMillis;
    }

    //public void animateModel(Bitmap[] bitmaps, long interval)

    /**
     * @param curTimeMillis
     * @return returns true if currently visible. False otherwise.
     */
    public boolean hit(long curTimeMillis) {
        m_state.actionHit();
        return isVisible(curTimeMillis);
    }

    public boolean checkIfTimeOut(long curTimeMillis) {
        if (!isVisible(curTimeMillis)) { //if time out (so it became invisible)
            m_state.actionTimeOut();
            if (m_state.getState() == State.Invisible && m_state.getTimeoutPenalty())
                return true;
        }
        return false;
    }

    private boolean isVisible(long curTimeMillis) {
        return curTimeMillis < m_expirationTime;
    }

    protected enum State {Visible, Invisible}

    //FSM approach
    private class ModelState {
        private State m_state;
        private boolean m_timeoutPenalty;

        protected ModelState() {
            m_state = State.Invisible;
            m_timeoutPenalty = false;
        }

        public State getState() {
            return m_state;
        }

        public boolean getTimeoutPenalty() {
            return m_timeoutPenalty;
        }

        public void actionHit() {
            if (m_state == State.Invisible) {
                m_timeoutPenalty = true;
            } else if (m_state == State.Visible) {
                m_state = State.Invisible;
                m_timeoutPenalty = false;
            }
        }

        public void actionTimeOut() {
            if (m_state == State.Invisible) {
                m_timeoutPenalty = false;
            } else if (m_state == State.Visible) {
                m_state = State.Invisible;
                m_timeoutPenalty = true;
            }
        }

        public void actionMakeVisible() {
            if (m_state == State.Invisible) {
                m_state = State.Visible;
                m_timeoutPenalty = false;
            } else if (m_state == State.Visible) {
                //not possible
            }
        }
    }
}
