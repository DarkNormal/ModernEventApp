package com.lordan.mark.PosseUp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;

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
        SharedPreferences settings = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if (!(settings == null)) {
            String token = settings.getString("access_token", null);
            String email = settings.getString("email", null);
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(token)) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                beginActivity(intent);
            }
            else {
                goToSignin();
            }
        }
        else {
            goToSignin();
        }


    }

    private void beginActivity(final Intent intent) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();   //finish the activity, don't want users going back to this screen
            }
        }, 2000);
    }

    private void goToSignin() {
        Intent intent = new Intent(StartActivity.this, SigninActivity.class);
        beginActivity(intent);
    }
}
