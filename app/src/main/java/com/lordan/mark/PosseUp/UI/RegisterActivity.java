package com.lordan.mark.PosseUp.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.ConfigureAccount;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import java.net.MalformedURLException;

/**
 * Created by Mark on 9/30/2015.
 */
public class RegisterActivity extends AbstractActivity {

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
        FloatingActionButton addImage = (FloatingActionButton) findViewById(R.id.addImage_Button);
        addImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Functionality coming soon!", Toast.LENGTH_SHORT);
                toast.show();
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
            final User newUser = new User(email.getText().toString(), password.getText().toString(), fullName.getText().toString() );
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
            if(connected){
                mProgressDialog = ProgressDialog.show(this, "Registering",
                        "Pretending to look busy...", true);


                ListenableFuture<User> result = mobileServiceClient.invokeApi( "register", newUser, User.class );

                Futures.addCallback(result, new FutureCallback<User>() {
                    @Override
                    public void onFailure(Throwable exc) {
                        mProgressDialog.dismiss();
                        createAndShowDialog((Exception) exc, "Error");
                    }

                    @Override
                    public void onSuccess(User result) {
                        mProgressDialog.dismiss();
                        if(result.isRegistered()){
                            newUser.setToken(result.getToken());
                            newUser.setUserId(result.getUserId());
                            MobileServiceUser user = new MobileServiceUser(result.getUserId());
                            user.setAuthenticationToken(result.getToken());
                            mobileServiceClient.setCurrentUser(user);
                            AzureService az = new AzureService();
                            az.saveUserData(getApplicationContext(), mobileServiceClient, newUser.getUsername(), newUser.getEmail());
                            SharedPreferences settings = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
                            String userId;
                            if(settings != null){
                                userId = settings.getString("userId", null);
                                System.out.println(userId + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }
                            Intent intent = new Intent(RegisterActivity.this, ConfigureAccount.class);
                            intent.putExtra("name", newUser.getFullname());
                            startActivity(intent);
                            finish();
                        }
                        else{
                            email.setError("This email already has an account");
                        }
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

