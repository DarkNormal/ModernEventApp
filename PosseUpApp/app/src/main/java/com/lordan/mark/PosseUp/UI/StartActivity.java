package com.lordan.mark.PosseUp.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.lordan.mark.PosseUp.UI.SigninGroup.SigninActivity;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Mark on 8/31/2015.
 * Activity to prepare app for launch
 * Fullscreen with space available for a logo or something
 */
public class StartActivity extends AbstractActivity {
    private final Handler mHandler = new Handler();
    private final String SENDER_ID = "851010273767";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private static final String TAG = "StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig("1IYKulpvPMfNOTX88TGMMB5KZ", "Ozzrm0w697wRB5F2E8GzYVfjRcBBw1fy35GMpEUo89MTfjqKvH");
        Fabric.with(getApplicationContext(), new Crashlytics(), new Twitter(authConfig));
        setupGcm();

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

    private void setupGcm() {
        MyHandler.startActivity = this;
        NotificationsManager.handleNotifications(this, SENDER_ID, MyHandler.class);
        gcm = GoogleCloudMessaging.getInstance(this);
        String hubListenConnectionString = "Endpoint=sb://posseup-notificationhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=ULoYyvlyIcvWKY1XFmUYB8OGNTOfzIKHT11m1AukTuc=";
        String hubName = "PosseUp-Notifications";
        hub = new NotificationHub(hubName, hubListenConnectionString, this);
        registerWithNotificationHubs();
    }

    @SuppressWarnings("unchecked")
    private void registerWithNotificationHubs() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(SENDER_ID);
                    Log.i(TAG,"Registered Successfully - RegID: " + hub.register(regid, getCurrentEmail()).getRegistrationId());
                } catch (Exception e) {
                    Log.e(TAG, "GCM register exception");
                }
                return null;
            }
        }.execute(null, null, null);
    }
}
