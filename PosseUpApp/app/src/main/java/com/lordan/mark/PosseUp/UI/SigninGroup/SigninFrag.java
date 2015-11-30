package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;

import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;

import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivity;
import com.lordan.mark.PosseUp.UI.RegisterActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 10/10/2015.
 */
public class SigninFrag extends Fragment implements View.OnClickListener {
    private View detailsView;
    private EditText username, password;
    private RequestQueue queue;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsView = inflater.inflate(R.layout.signinfrag_layout, container, false);
        queue = Volley.newRequestQueue(getActivity());
        TextView forgotPassword = (TextView) detailsView.findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  //sendEmail();      //send email with reset instructions
                                                  Toast.makeText(getActivity(), "Functionality coming soon!", Toast.LENGTH_SHORT).show();

                                              }
                                          }

        );
        TextView signUp = (TextView) detailsView.findViewById(R.id.signup_text);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        Button signInButton = (Button) detailsView.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = (EditText) detailsView.findViewById(R.id.username_signin);
                password = (EditText) detailsView.findViewById(R.id.password);
                if (!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
                    mProgressDialog = new ProgressDialog(getActivity());
                    login(username, password);
                } else {
                    if (username.getText().toString().isEmpty()) {
                        username.setError("Cannot be blank");
                    }
                    if (password.getText().toString().isEmpty()) {
                        password.setError("Cannot be blank");
                    }
                }

            }

        });
        return detailsView;
    }

    @Override
    public void onClick(View v) {

    }

    public static SigninFrag newInstance() {
        SigninFrag myFragment = new SigninFrag();
        return myFragment;
    }

    private void login(final EditText username, final EditText password) {

        mProgressDialog = ProgressDialog.show(getActivity(), "Logging in",
                "1 moment...", true);
//
        String url = Constants.baseUrl + "api/Account/TokenLogin";
        JSONObject jobject = new JSONObject();

        try {
            jobject.put("Username", username.getText().toString());
            jobject.put("Password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.length());
                login(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
                mProgressDialog.dismiss();
                String json;

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
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    private void login(final JSONObject jsonBody) {

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
                try {
                    AzureService az = new AzureService();
                    az.saveUserData(getActivity(), json.getString("access_token"), json.getString("userName"), json.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("No Token recieved");
                mProgressDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
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
    public String trimMessage(String json, String key) {
        String error="";

        try {
            JSONObject obj = new JSONObject(json);
            error = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return error;
    }

    public void setErrors(String toastString, EditText username, EditText email) {

                if (toastString.startsWith("Account")) {
                    Toast.makeText(getActivity(), "An account with this email/username and password combination was not found", Toast.LENGTH_LONG).show();
                    password.setText("");
                } else if (toastString.startsWith("invalid")) {
                    Toast.makeText(getActivity(), "Invalid credentials entered", Toast.LENGTH_SHORT).show();
                    password.setText("");
                }
            }


}
