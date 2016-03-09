package com.lordan.mark.PosseUp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

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
        TwitterAuthConfig authConfig = new TwitterAuthConfig("1IYKulpvPMfNOTX88TGMMB5KZ", "Ozzrm0w697wRB5F2E8GzYVfjRcBBw1fy35GMpEUo89MTfjqKvH");
        Fabric.with(getApplicationContext(), new Crashlytics(), new Twitter(authConfig));

        FacebookSdk.sdkInitialize(getApplicationContext());
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
        }, 1000);
    }

    private void goToSignin() {
        Intent intent = new Intent(StartActivity.this, SigninActivity.class);
        beginActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
