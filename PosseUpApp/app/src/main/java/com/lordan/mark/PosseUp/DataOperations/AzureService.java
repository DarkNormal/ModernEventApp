package com.lordan.mark.PosseUp.DataOperations;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Mark on 10/3/2015.
 */
public class AzureService {
    public AzureService(){

    }

    public void saveUserData(Context context, String access_token, String email) {
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("access_token", access_token);
        preferencesEditor.putString("username", email);
        preferencesEditor.commit();
    }
    public String getCurrentUsername(Context context){
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        return settings.getString("username", null);
    }
}
