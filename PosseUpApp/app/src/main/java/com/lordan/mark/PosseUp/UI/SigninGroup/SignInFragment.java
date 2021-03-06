package com.lordan.mark.PosseUp.UI.SigninGroup;



import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;

import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivityGroup.MainActivity;
import com.lordan.mark.PosseUp.VolleyCallback;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mark on 10/10/2015
 */
public class SignInFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.username_signin)
    public EditText username;
    @Bind(R.id.password)
    public EditText password;
    @Bind(R.id.login_button)
    public LoginButton facebookLoginButton;
    @Bind(R.id.twitter_login_button)
    public TwitterLoginButton twitterLoginButton;
    @Bind(R.id.signup_text)
    public TextView signUp;
    @Bind(R.id.signin_button)
    public Button signInButton;
    private RequestQueue queue;

    private ProgressDialog mProgressDialog;
    private static final String TAG = "SignInFrag";
    private CallbackManager callbackManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.signinfrag_layout, container, false);
        ButterKnife.bind(this, v);
        queue = Volley.newRequestQueue(getActivity());

        facebookLoginButton.setReadPermissions("user_friends", "email");
        facebookLoginButton.setFragment(this);
        facebookCallbackSetup(facebookLoginButton);

        twitterCallbackSetup(twitterLoginButton);
        signInButton.setOnClickListener(this);
        username.addTextChangedListener(getTextWatcher());
        password.addTextChangedListener(getTextWatcher());
        password.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    signInButton.performClick();
                    return true;
                }
                return false;
            }
        });
        signUp.setOnClickListener(this);

        return v;
    }

    private void enableSignIn(){
        if(!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())){
            signInButton.setEnabled(true);
            signInButton.setAlpha(1.0f);
        }
        else if(signInButton.getAlpha() != 0.5f){
            signInButton.setEnabled(false);
            signInButton.setAlpha(0.5f);
        }
    }
    private void twitterCallbackSetup(TwitterLoginButton twitterLoginButton){
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }
    private void facebookCallbackSetup(LoginButton facebookLoginButton){
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FACEBOOK LOGIN", loginResult.getAccessToken().toString() + "," + loginResult.getAccessToken().getUserId());

                ExternalLogin externalLogin = new ExternalLogin("Facebook", loginResult.getAccessToken().getToken());
                Gson gson = new Gson();

                String convertedLogin = gson.toJson(externalLogin);
                JSONObject postObject = null;
                try {
                    postObject = new JSONObject(convertedLogin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loginExternal(postObject);

            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, exception.toString());
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE){
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
        else {
            // TODO figure out request code for facebook callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void login() {
        mProgressDialog = getmProgressDialog();
        mProgressDialog.show();
        String url = Constants.baseUrl + "api/Account/TokenLogin";
        JSONObject jobject = new JSONObject();

        try {
            jobject.put("Username", username.getText().toString());
            jobject.put("Password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        signInProcess(Request.Method.POST, url, jobject, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                mProgressDialog.dismiss();
                saveRetrievedData(1,result);
                loginLocal(result);
            }

            @Override
            public void onError(VolleyError error) {
                String json;
                mProgressDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorReply;
                    switch (response.statusCode) {
                        case 400:
                            json = new String(response.data);
                            errorReply = trimMessage(json, "Message");
                            if (errorReply != null) setErrors(errorReply, username, password);
                            break;
                        case 412:
                            json = new String(response.data);
                            errorReply = trimMessage(json, "Message");
                            System.out.println(errorReply);
                            if(errorReply != null) setErrors(errorReply, username, password);
                            break;
                        default:
                            Toast.makeText(getActivity(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getContext(), "Unable to contact Posse Up servers, please try again in a moment", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private void signInProcess(int method, String url, JSONObject jobject, final VolleyCallback volleyCallback){
        JsonObjectRequest req = new JsonObjectRequest(method, url, jobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                volleyCallback.onError(error);
            }
        });
        queue.add(req);
    }


    private void loginExternal(final JSONObject jsonBody) {
        mProgressDialog = getmProgressDialog();
        mProgressDialog.show();
        String url = Constants.baseUrl + "api/Account/RegisterExternal";
        signInProcess(Request.Method.POST, url, jsonBody, new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                mProgressDialog.dismiss();
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Facebook")
                        .putSuccess(true));
                saveRetrievedData(2,result);
            }

            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                NetworkResponse networkResponse = null;
                try{
                    networkResponse = error.networkResponse;
                }
                catch(Exception e){
                    Log.i(TAG, "Null network response");
                }
                if(networkResponse != null) {
                    switch (networkResponse.statusCode){
                        case 400:
                            if(new String(networkResponse.data).contains("is already taken")){
                                Log.e(TAG,"Username taken");
                                retryExternalLogin(jsonBody);
                            }
                            break;
                        case 403:
                        case 500:
                            LoginManager.getInstance().logOut();
                            Toast.makeText(getContext(), "Unable to contact Posse Up servers, please try again in a moment", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                else{
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getContext(), "Unable to contact Posse Up servers, please try again in a moment", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void saveRetrievedData(int methodID, JSONObject json){
        AzureService az = new AzureService();
        if(methodID == 1){
            try {
                az.saveProfileImage(getActivity(), json.getString("ProfilePicture"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(methodID == 2) {
            try {
                az.saveUserData(getActivity(), json.getString("access_token"), json.getString("userName"), json.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

    }
    private void loginLocal(final JSONObject jsonBody) {
        String url = Constants.baseUrl + "Token";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject json = new JSONObject();
                System.out.println("Token recieved");
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Answers.getInstance().logLogin(new LoginEvent()
                        .putMethod("Custom")
                        .putSuccess(true));
                saveRetrievedData(2,json);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No Token recieved");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "password");
                try {
                    params.put("username", jsonBody.getString("Username"));
                    params.put("password", jsonBody.getString("Password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String httpPostBody = null;
                try {
                    httpPostBody = "grant_type=password&username=" + jsonBody.getString("Username") + "&password=" + jsonBody.getString("Password");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return httpPostBody.getBytes();
            }
        };
        queue.add(req);

    }
    private void retryExternalLogin(final JSONObject jsonObject){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.CustomAlertDialogStyle));

        //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Facebook username");

        // Setting Dialog Message
        alertDialog.setMessage("Please enter a username for Posse Up");
        final EditText input = new EditText(getContext());
        input.setTextColor(0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoginManager.getInstance().logOut();
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(input.getText())) {
                    try {
                        jsonObject.put("Name", input.getText().toString());
                        dialog.dismiss();
                        loginExternal(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    input.setError("Enter a username");
                }
            }
        });
    }

    private String trimMessage(String json, String key) {
        String error;

        try {
            JSONObject obj = new JSONObject(json);
            error = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return error;
    }

    private void setErrors(String toastString, EditText username, EditText email) {

                if (toastString.startsWith("Account")) {
                    Toast.makeText(getActivity(), "An account with this email/username and password combination was not found", Toast.LENGTH_LONG).show();
                    password.setText("");
                } else if (toastString.startsWith("invalid")) {
                    Toast.makeText(getActivity(), "Invalid credentials entered", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
            }
    private TextWatcher getTextWatcher(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                enableSignIn();
            }
        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.signup_text:
                ((SigninActivity)getActivity()).switchToRegister();
                break;
            case R.id.signin_button:
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                login();
                break;


        }
    }
    class ExternalLogin{
         public ExternalLogin(String provider, String token) {
             Provider = provider;
             Token = token;
         }

         @SerializedName("Provider")
         private final String Provider;
         @SerializedName("ExternalAccessToken")
        private final String Token;

    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private ProgressDialog getmProgressDialog(){
        ProgressDialog mDialog = new ProgressDialog(getContext(), R.style.CustomAlertDialogStyle);
        mDialog.setTitle("Logging in");
        mDialog.setMessage("1 moment...");
        return mDialog;
    }


}

