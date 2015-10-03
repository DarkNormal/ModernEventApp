package com.lordan.mark.PosseUp.DataOperations;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

/**
 * Created by Mark on 10/3/2015.
 */
public class AzureService {
    public AzureService(){

    }

    public void saveUserData(Context context, MobileServiceClient mClient, String mUsername, String mEmail) {
        SharedPreferences settings = context.getSharedPreferences("PosseUpData", Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = settings.edit();
        preferencesEditor.putString("userid", mClient.getCurrentUser().getUserId());
        preferencesEditor.putString("token", mClient.getCurrentUser().getAuthenticationToken());
        preferencesEditor.putString("username", mUsername);
        preferencesEditor.putString("email", mEmail);
        preferencesEditor.commit();
    }
}
