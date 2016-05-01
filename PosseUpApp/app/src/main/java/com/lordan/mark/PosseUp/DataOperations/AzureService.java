package com.lordan.mark.PosseUp.DataOperations;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


/**
 * Created by Mark on 10/3/2015
 */
public class AzureService {
    public AzureService(){

    }

    public void saveUserData(Context context, String access_token,String username, String email) {
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("access_token", access_token);
        preferencesEditor.putString("email", email);
        preferencesEditor.putString("username", username);
        preferencesEditor.apply();
    }
    public String getCurrentUsername(Context context){
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        String result = "";
        try{
            result = settings.getString("username", null).toLowerCase();
        }
        catch(NullPointerException npe){

            String TAG = "AzureService";
            Log.e(TAG, "username null");
        }
        return result;
    }
    public String getCurrentEmail(Context context){
        SharedPreferences prefs = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("email", null);
        }
        return null;
    }
    public String getToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        if(prefs != null){
            return prefs.getString("access_token", null);
        }
        return null;
    }

    public void saveProfileImage(Context context, String imageUrl){
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("profileImageURL", imageUrl);
        preferencesEditor.apply();
    }
    public String getProfileImageURL(Context context){
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        return settings.getString("profileImageURL", null);
    }
}
