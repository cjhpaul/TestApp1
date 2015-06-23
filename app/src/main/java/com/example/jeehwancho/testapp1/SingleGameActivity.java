package com.example.jeehwancho.testapp1;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class SingleGameActivity extends Activity {
    private static final String TAG = SingleGameActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate has started.");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new SingleGamePanel(this));
        Log.d(TAG, "onCreate has ended.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy has ended.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop has ended.");
    }
}
