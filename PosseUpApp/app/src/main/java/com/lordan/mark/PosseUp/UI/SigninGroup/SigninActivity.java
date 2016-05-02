package com.lordan.mark.PosseUp.UI.SigninGroup;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.Model.MyHandler;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.microsoft.windowsazure.messaging.NotificationHub;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


/**
 * Created by Mark on 8/31/2015
 */
public class SigninActivity extends AbstractActivity {

    private Handler mHandler = new Handler();
    private static final String TAG = "SignInActivity";

    @Bind(R.id.fragmentHolder)
    public LinearLayout fragmentHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig("1IYKulpvPMfNOTX88TGMMB5KZ", "Ozzrm0w697wRB5F2E8GzYVfjRcBBw1fy35GMpEUo89MTfjqKvH");
        Fabric.with(getApplicationContext(), new Crashlytics(), new Twitter(authConfig));

        setContentView(R.layout.signin_layout);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());

        SharedPreferences settings = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if (!(settings == null)) {
            String token = settings.getString("access_token", null);
            String email = settings.getString("email", null);
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(token)) {
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                beginActivity(intent);
            }
            else {
                displaySignInFragment(savedInstanceState);
            }
        }
        else {
            displaySignInFragment(savedInstanceState);
        }


    }
    private void displaySignInFragment(Bundle savedInstanceState){
        if(savedInstanceState == null){
            Fragment myFrag = new SignInFragment();
            getSupportFragmentManager().beginTransaction().add(fragmentHolder.getId(), myFrag, "signin_fragment").commit();
        }
    }
    public void switchToRegister(){
        Fragment registerFrag = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().replace(fragmentHolder.getId(), registerFrag).addToBackStack("register").commit();

    }
    private void beginActivity(final Intent intent) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();   //finish the activity, don't want users going back to this screen
            }
        }, 700);
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //myFrag.onActivityResult(requestCode,resultCode,data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        ImageView background = (ImageView)findViewById(R.id.signin_background);
        if(background != null) {
            background.setImageDrawable(null);
        }
        mHandler = null;
    }
}



