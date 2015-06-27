package com.example.jeehwancho.testapp1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class SingleGameActivity extends Activity {
    private static final String TAG = SingleGameActivity.class.getSimpleName();
    private SingleGamePanel m_singleGamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate has started.");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        m_singleGamePanel = new SingleGamePanel(this);
        setContentView(m_singleGamePanel);
        Log.d(TAG, "onCreate has ended.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("singleGameScore", String.valueOf(m_singleGamePanel.getScore()));
        startActivity(intent);
        Log.d(TAG, "onDestroy has ended.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop has ended.");
    }
}
