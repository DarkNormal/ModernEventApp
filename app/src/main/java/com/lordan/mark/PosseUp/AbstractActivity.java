package com.lordan.mark.PosseUp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.lordan.mark.PosseUp.Model.Constants;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.net.MalformedURLException;

/**
 * Created by Mark on 7/18/2015.
 * Abstract activity to set orientation of all Activities to Portrait
 * Every activity is a child of this class, or should be anyway
 */

public abstract class AbstractActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    protected boolean isValidEmail(CharSequence target){
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    protected boolean isValidPassword(String target) {
        return target.isEmpty() == false && target.length() > 6;
    }

    protected static MobileServiceClient mobileServiceClient;
    protected  static MobileServiceUser mobileServiceUser;
    protected static String mFullName, mEmail;

    protected boolean setMobileServiceClient(){
        boolean connected = true;
        try {
            mobileServiceClient = new MobileServiceClient(
                    Constants.MOBILE_SERVICE_URL,
                    Constants.MOBILE_SERVICE_APPLICATION_KEY,
                    this);
            // .withFilter(new ProgressFilter());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            connected = false;
        }
        return connected;
    }

    protected void setUserData(String userId, String token, String fullName, String email){
        mobileServiceUser = new MobileServiceUser(userId);
        mobileServiceUser.setAuthenticationToken(token);
        mobileServiceClient.setCurrentUser(mobileServiceUser);
        mFullName = fullName;
        mEmail = email;


    }
    protected void singOut(){
        SharedPreferences settings = getSharedPreferences("PosseUpData", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("userId");
        editor.remove("token");
        editor.remove("username");
        editor.remove("email");
        editor.commit();
    }
}
