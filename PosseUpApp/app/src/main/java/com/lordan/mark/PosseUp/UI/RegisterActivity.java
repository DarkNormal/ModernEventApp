package com.lordan.mark.PosseUp.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
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
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.Constants;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 9/30/2015.
 */
public class RegisterActivity extends AbstractActivity {

    private ProgressDialog mProgressDialog; //dialog used for all Volley requests
    private RequestQueue queue;             //Volley queue

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Button signup = (Button) findViewById(R.id.signup_button);
        queue = Volley.newRequestQueue(this);       //instantiate Volley queue
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username_register);
                EditText email = (EditText) findViewById(R.id.email_signup);
                EditText password = (EditText) findViewById(R.id.password_signup);

                if (validateDetails(username, email, password)) {
                    registerUser(username, email, password);     //begin registration process
                }

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

    public void registerUser(final EditText username, final EditText email, EditText password) {

        final User newUser = new User(email.getText().toString(), password.getText().toString(), username.getText().toString());
        mProgressDialog = ProgressDialog.show(this, "Registering",
                "Pretending to look busy...", true);
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Email", newUser.getEmail());
            jsonBody.put("Password", newUser.getPassword());
            jsonBody.put("Username", newUser.getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = Constants.baseUrl + "api/Account/Register";
        JsonObjectRequest jrequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                mProgressDialog.dismiss();
                System.out.println("user registered");
                login(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                System.out.println(error.networkResponse.statusCode);
                String json;

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    switch (response.statusCode) {
                        case 400:
                            json = new String(response.data);
                            JSONArray jsonarray = trimMessage(json, "Errors");
                            if (jsonarray != null) setErrors(jsonarray, username, email);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "An unknown error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
                System.out.println("User not registered");
            }
        });
        jrequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jrequest);

    }

    private boolean validateDetails(EditText username, EditText email, EditText password) {
        boolean proceedRegister1, proceedRegister2, proceedRegister3;
        if (!isValidUsername(username.getText().toString())) {
            username.setError("Invalid username, cannot be empty and cannot contain @");
            proceedRegister1 = false;
        } else proceedRegister1 = true;
        if (!isValidEmail(email.getText().toString())) {
            email.setError("Invalid Email");
            proceedRegister2 = false;
        } else proceedRegister2 = true;
        if (!isValidPassword(password.getText().toString())) {
            password.setError("Mixed case letters and at least 1 digit is required. Length must be > 6");
            proceedRegister3 = false;
        } else proceedRegister3 = true;

        return proceedRegister1 == true && proceedRegister2 == true && proceedRegister3 == true;
    }

    private void login(final JSONObject jsonBody) {
        ProgressDialog.show(this, "Logging in",
                "1 moment...", true);
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
                    az.saveUserData(getApplicationContext(), json.getString("access_token"), jsonBody.getString("Email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
                    params.put("username", jsonBody.getString("Email"));
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

    public JSONArray trimMessage(String json, String key) {
        JSONArray jarray;

        try {
            JSONObject obj = new JSONObject(json);
            jarray = obj.getJSONArray(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jarray;
    }

    public void setErrors(JSONArray toastString, EditText username, EditText email) {
        for (int i = 0; i < toastString.length(); i++) {
            try {
                if (toastString.getString(i).startsWith("Name")) {
                    username.setError("Username is taken");
                } else if (toastString.getString(i).startsWith("Email")) {
                    email.setError("Email is taken");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

