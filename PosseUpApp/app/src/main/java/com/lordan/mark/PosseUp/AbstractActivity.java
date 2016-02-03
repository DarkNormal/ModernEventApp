package com.lordan.mark.PosseUp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mark on 7/18/2015.
 * Abstract activity to set orientation of all Activities to Portrait
 * Every activity is a child of this class, or should be anyway
 */

public abstract class AbstractActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    protected boolean isValidEmail(String target){
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    protected boolean isValidUsername(String target){
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return !target.contains("@");
        }
    }

    protected boolean isValidPassword(String target) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(target);

        return matcher.matches();
    }

    protected void signOut(){       //logout user completely, remove all login information
        SharedPreferences settings = getSharedPreferences("PosseUpData", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("token");
        editor.remove("email");
        editor.commit();
    }
    public String getCurrentEmail(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("email", null);
        }
        return null;
    }
    protected String getCurrentUsername(){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("username", null);
        }
        return null;
    }
}
