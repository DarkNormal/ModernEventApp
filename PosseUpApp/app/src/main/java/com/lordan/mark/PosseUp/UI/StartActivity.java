package com.lordan.mark.PosseUp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

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
        if(setMobileServiceClient()){
            SharedPreferences settings = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
            if(!(settings == null)){
                String userId = settings.getString("userId", null);
                String token = settings.getString("token", null);
                String username = settings.getString("username", null);
                String email = settings.getString("email", null);
                if (userId != null && !userId.equals("")) {
                    setUserData(userId, token, username, email);
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    beginActivity(intent);
                }
                else{
                    Intent intent = new Intent(StartActivity.this, SigninActivity.class);
                    beginActivity(intent);
                }
            }
            else{
                Intent intent = new Intent(StartActivity.this, SigninActivity.class);
                beginActivity(intent);
            }
        }


    }
    private void beginActivity(final Intent intent){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();   //finish the activity, don't want users going back to this screen
            }
        }, 2000);
    }
}
