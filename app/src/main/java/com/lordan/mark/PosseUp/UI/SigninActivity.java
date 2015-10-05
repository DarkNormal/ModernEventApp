package com.lordan.mark.PosseUp.UI;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.LanguageHelper;

import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

/**
 * Created by Mark on 8/31/2015.
 */
public class SigninActivity extends AbstractActivity {

    public static MobileServiceClient mobileServiceClient;
    private ProgressDialog mProgressDialog;
    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.changeLocale(this.getResources(), "en");
        connectMobileService();
        setContentView(R.layout.signin_layout);
        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        TextView signUp = (TextView) findViewById(R.id.signup_text);
        makeTextClickable(forgotPassword);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, RegisterActivity.class);
                startActivity(intent);      //no need to finish this activity here, could cause a loop of activity stack
                //could also cause app to close completely from registerActivity
            }
        });
        Button signInButton = (Button) findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connectMobileService()){

                    username = (TextView) findViewById(R.id.username_email);
                    TextView password = (TextView) findViewById(R.id.password);
                    if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                        mProgressDialog = ProgressDialog.show(SigninActivity.this, "Logging in",
                                "Please wait...", true);
                        login(username.getText().toString(), password.getText().toString());
                    }
                    else{
                        if(username.getText().toString().isEmpty()){
                            username.setError("Cannot be blank");
                        }
                        if(password.getText().toString().isEmpty()){
                            password.setError("Cannot be blank");
                        }
                    }

                }
                else{
                    System.out.println("dang");
                }
            }
        });

    }

    private boolean connectMobileService() {
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

    //    @Override
//    public void onBackPressed() {
//        /*Override the back button action on sign in activity
//        * This prevents the return to StartActivity (logo screen)
//        */
//            moveTaskToBack(true);
//    }
    public void makeTextClickable(TextView toClick) {    //adds click listener to any TextViews within this activity
        toClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int duration = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Clicked!", duration);
                toast.show();
            }
        });

    }

    private void login(String usernameOrEmail, String password) {

        User user = new User();
        user.setEmailOrUsername(usernameOrEmail);
        user.setPassword(password);
        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("login", user, JsonElement.class );

        Futures.addCallback(result, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                System.out.println("hooray!");
                if (result.isJsonObject()) {
                    JsonObject resultObj = result.getAsJsonObject();
                    if (resultObj.get("status").getAsString().equals("SUCCESS")) {
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //incorrect username/password
                        username.setError("Incorrect username or password");
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
