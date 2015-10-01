package com.lordan.mark.PosseUp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.lordan.mark.PosseUp.Model.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.net.MalformedURLException;

/**
 * Created by Mark on 9/30/2015.
 */
public class RegisterActivity extends AbstractActivity{

    private MobileServiceClient mobileServiceClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Button signup = (Button) findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                registerUser();
            }
        });
        TextView backToSignIn = (TextView) findViewById(R.id.signin_text);
        backToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();       //finish the current activity, goes to last activity in stack (sign in activity)
            }
        });
    }

    public void registerUser(){
        final EditText fullName = (EditText) findViewById(R.id.fullname);
        final EditText email = (EditText) findViewById(R.id.email_signup);
        final EditText password = (EditText) findViewById(R.id.password_signup);
        if(fullName.getText().toString().isEmpty()){
            fullName.setError("Please enter your name");
        }
        if(!isValidEmail(email.getText().toString())){
            email.setError("Invalid Email");
        }
        if(!isValidPassword(password.getText().toString())){
            password.setError("Invalid Password");
        }
        else{
            User newUser = new User(email.getText().toString(), password.getText().toString(), fullName.getText().toString());
            boolean connected = true;
            try {
                mobileServiceClient = new MobileServiceClient(
                        "https://markrecipe.azure-mobile.net/",
                        "PARUUkHfFBFlppgVDdVjorhICtRehq99",
                        this);
                // .withFilter(new ProgressFilter());
            } catch (MalformedURLException e) {
                System.out.println(e.getMessage());
                connected = false;
            }
            if(connected){
                mProgressDialog = ProgressDialog.show(this, "dialog title",
                        "dialog message", true);


                ListenableFuture<User> result = mobileServiceClient.invokeApi( "register", newUser, User.class );

                Futures.addCallback(result, new FutureCallback<User>() {
                    @Override
                    public void onFailure(Throwable exc) {
                        createAndShowDialog((Exception) exc, "Error");
                    }

                    @Override
                    public void onSuccess(User result) {
                        //createAndShowDialog(result.isRegistered() + " is registered", "Completed Items");
                        mProgressDialog.dismiss();
                    }
                });
            }
            }
        }
    private void createAndShowDialog(Exception e, String title){
        createAndShowDialog(e.toString(), title);
    }
    private void createAndShowDialog(String message, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
    }

