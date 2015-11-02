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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark on 9/30/2015.
 */
public class RegisterActivity extends AbstractActivity {

    private MobileServiceClient mobileServiceClient = AbstractActivity.mobileServiceClient;
    private ProgressDialog mProgressDialog;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Button signup = (Button) findViewById(R.id.signup_button);
        queue  = Volley.newRequestQueue(this);
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
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Functionality coming soon!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void registerUser() {
        boolean proceedRegister1, proceedRegister2, proceedRegister3;
        final EditText username = (EditText) findViewById(R.id.username_register);
        final EditText email = (EditText) findViewById(R.id.email_signup);
        final EditText password = (EditText) findViewById(R.id.password_signup);
        if (!isValidUsername(username.getText().toString())) {
            username.setError("Invalid username, cannot be empty and cannot contain @");
            proceedRegister1 = false;
        } else proceedRegister1 = true;
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Invalid Email");
            proceedRegister2 = false;
        } else proceedRegister2 = true;
        if (!isValidPassword(password.getText().toString())) {
            password.setError("Invalid Password, cannot be empty and must be longer than 6 characters");
            proceedRegister3 = false;
        } else proceedRegister3 = true;

        if (proceedRegister1 == true && proceedRegister2 == true && proceedRegister3 == true) {
            final User newUser = new User(email.getText().toString(), password.getText().toString(), username.getText().toString());
            mProgressDialog = ProgressDialog.show(this, "Registering",
                    "Pretending to look busy...", true);
            final JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Name", newUser.getUsername());
                jsonBody.put("Email", newUser.getEmail());
                jsonBody.put("Password", newUser.getPassword());
                jsonBody.put("Username", newUser.getUsername());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Instantiate the RequestQueue.

            String url = Constants.baseUrl + "Register";
            queue.add(new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    response.remove("AttendedEvents");
                    response.remove("UserID");
                    response.remove("Name");
                    response.remove("Username");
                    login(response);
                    System.out.println("cool beans");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    System.out.println(error.networkResponse.toString());
                    System.out.println("not cool beans");
                }
            }));
//                ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi( "CustomRegistration", newUser, JsonElement.class );
//
//                Futures.addCallback(result, new FutureCallback<JsonElement>() {
//                    @Override
//                    public void onFailure(Throwable exc) {
//                        mProgressDialog.dismiss();
//                        System.out.println("onFailure Register User");
//                        System.out.println(exc.getMessage());
//                    }
//
//                    @Override
//                    public void onSuccess(JsonElement result) {
//                        mProgressDialog.dismiss();
//                        if(result.isJsonObject()){
//                            JsonObject resultObj = result.getAsJsonObject();
//                            if(resultObj.get("message").getAsString().equals("account created")){
//                                login(username, email, password);
//                            }
////                            AzureService az = new AzureService();
////                            az.saveUserData(getApplicationContext(), mobileServiceClient, newUser.getUsername(), newUser.getEmail());
////                            SharedPreferences settings = getApplicationContext().getSharedPreferences("PosseUpData", MODE_PRIVATE);
////                            String userId;
////                            if(settings != null){
////                                userId = settings.getString("userId", null);
////                                System.out.println(userId + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
////                            }
////                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
////                            intent.putExtra("name", newUser.getUsername());
////                            startActivity(intent);
////                            finish();
//                        }
//                        else{
//                            email.setError("This email already has an account");
//                        }
//                    }
//                });
        }
    }

    private void login(JSONObject jsonBody) {
        mProgressDialog.show(this, "Logging in",
                "1 moment...", true);
        String email = null;
        String password = null;
        try {
            email = jsonBody.getString("Email");
            password = jsonBody.getString("Password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = String.format(Constants.baseUrl + "Login/%s/%s", email, password);
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                System.out.println("cool beans");
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                System.out.println(error.networkResponse.toString());
                System.out.println("not cool beans");
            }
        }));
//        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("CustomLogin", user, JsonElement.class);
//
//        Futures.addCallback(result, new FutureCallback<JsonElement>() {
//            @Override
//            public void onSuccess(JsonElement result) {
//                if (result.isJsonObject()) {
//                    JsonObject resultObj = result.getAsJsonObject();
//                    if (resultObj.get("userId").getAsString().contains("custom:")) {
//                        MobileServiceUser mUser = new MobileServiceUser(resultObj.get("userId").getAsString());
//                        mUser.setAuthenticationToken(resultObj.get("mobileServiceAuthenticationToken").toString());
//                        mobileServiceClient.setCurrentUser(mUser);
//                        AzureService az = new AzureService();
//                        az.saveUserData(getApplicationContext(), mobileServiceClient, user.getUsername(), user.getEmail());
//                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                        mProgressDialog.dismiss();
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        //incorrect username/password
//
//                        mProgressDialog.dismiss();
//                    }
//
//                } else {
//                    System.out.println("dang");
//                    mProgressDialog.dismiss();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable exc) {
//                System.out.println("onFailure Signin User");
//                mProgressDialog.dismiss();
//            }
//
//
//        });
    }
}

