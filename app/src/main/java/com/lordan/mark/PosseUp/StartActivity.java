package com.lordan.mark.PosseUp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Mark on 8/31/2015.
 * Activity to prepare app for launch
 * Fullscreen with space available for a logo or something
 */
public class StartActivity extends AbstractActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        }, 2500);

    }
}
