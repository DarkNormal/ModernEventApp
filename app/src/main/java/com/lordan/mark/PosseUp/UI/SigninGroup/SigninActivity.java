package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.LanguageHelper;

import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivity;
import com.lordan.mark.PosseUp.UI.RegisterActivity;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.net.MalformedURLException;

/**
 * Created by Mark on 8/31/2015.
 */
public class SigninActivity extends AbstractActivity {

    public static MobileServiceClient mobileServiceClient;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.changeLocale(this.getResources(), "en");
        setContentView(R.layout.signin_layout);
        LinearLayout fragmentHolder = (LinearLayout) findViewById(R.id.fragmentHolder);
        FragmentManager fragMan = getFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();
        Fragment myFrag = new SigninFrag();
        fragTransaction.add(fragmentHolder.getId(), myFrag, "signin_fragment");
        fragTransaction.commit();


    }


    //    @Override
//    public void onBackPressed() {
//        /*Override the back button action on sign in activity
//        * This prevents the return to StartActivity (logo screen)
//        */
//            moveTaskToBack(true);
//    }


    public void login(String usernameOrEmail, String password) {

        final User user = new User();
        user.setEmailOrUsername(usernameOrEmail);
        user.setPassword(password);
        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("login", user, JsonElement.class);

        Futures.addCallback(result, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                System.out.println("hooray!");
                if (result.isJsonObject()) {
                    JsonObject resultObj = result.getAsJsonObject();
                    if (resultObj.get("status").getAsString().equals("SUCCESS")) {
                        MobileServiceUser mUser = new MobileServiceUser(resultObj.get("userId").getAsString());
                        mUser.setAuthenticationToken(resultObj.get("token").toString());
                        mobileServiceClient.setCurrentUser(mUser);
                        AzureService az = new AzureService();
                        az.saveUserData(getApplicationContext(), mobileServiceClient, user.getUsername(), user.getEmail());
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //incorrect username/password

                        mProgressDialog.dismiss();
                    }

                } else {
                    System.out.println("dang");
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Throwable exc) {
                System.out.println("boo-urns!");
                mProgressDialog.dismiss();
            }


        });
    }
}



