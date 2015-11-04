package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.lordan.mark.PosseUp.UI.MainActivity;
import com.lordan.mark.PosseUp.UI.RegisterActivity;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mark on 10/10/2015.
 */
public class SigninFrag extends Fragment implements View.OnClickListener{
    private View detailsView;
    private TextView username;
    private RequestQueue queue;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsView = inflater.inflate(R.layout.signinfrag_layout, container, false);
        queue  = Volley.newRequestQueue(getActivity());
        TextView forgotPassword = (TextView) detailsView.findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  sendEmail();      //send email with reset instructions

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
                username = (TextView) detailsView.findViewById(R.id.username_signin);
                TextView password = (TextView) detailsView.findViewById(R.id.password);
                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
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

        });
        return detailsView;
    }

    @Override
    public void onClick(View v) {

    }

    private void sendEmail(){
//        final TextView username = (TextView) detailsView.findViewById(R.id.username_signin);
//        final ProgressDialog mProgressDialog = ProgressDialog.show(getActivity(), "Resetting password",
//                "Please wait...", true);
//        if(!username.getText().toString().isEmpty()){
//
//            User forgetfulUser = new User();
//            forgetfulUser.setEmail(username.getText().toString());
//
//            ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("forgot_password",forgetfulUser, JsonElement.class);
//            Futures.addCallback(result, new FutureCallback<JsonElement>() {
//                @Override
//                public void onSuccess(JsonElement result) {
//                    if (result.getAsJsonObject().get("status").getAsString().equals("email sent")) {
//                        Toast.makeText(getActivity(), "Email sent to " + username.getText().toString(), Toast.LENGTH_SHORT).show();
//                        mProgressDialog.dismiss();
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
//
//                        ForgotPasswordFrag newFragment = ForgotPasswordFrag.newInstance();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("email", username.getText().toString());
//                        newFragment.setArguments(bundle);
//
//                        ft.replace(R.id.fragmentHolder, newFragment, "forgotpass_frag");
//                        ft.commit();
//                    } else {
//                        username.setError("No matching email found");
//                        mProgressDialog.dismiss();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    System.out.println("onFailure");
//                    mProgressDialog.dismiss();
//                }
//            });
//        }
//        else{
//            username.setError("Enter an email address");
//            mProgressDialog.dismiss();
//        }

    }
    public static SigninFrag newInstance()
    {
        SigninFrag myFragment = new SigninFrag();
        return myFragment;
    }
    private void login(String username, String password) {
//        mProgressDialog.show(getActivity(), "Logging in",
//                "1 moment...", true);
//
//
//        String url = String.format(Constants.baseUrl + "Token", username, password);
//        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                JSONObject json = new JSONObject();
//                System.out.println("Token recieved");
//                try {
//                    json = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    AzureService az = new AzureService();
//                    az.saveUserData(getActivity(), json.getString("access_token"), json.getString("Email"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                mProgressDialog.dismiss();
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//                getActivity().finish();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println("No Token recieved");
//                mProgressDialog.dismiss();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("grant_type", "password");
//                try {
//                    params.put("username", jsonBody.getString("Email"));
//                    params.put("password", jsonBody.getString("Password"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                return params;
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                String httpPostBody = "grant_type=password&username=mark.lordan@gmail.com&password=Supern00b-";
//                return httpPostBody.getBytes();
//            }
//        };
//        queue.add(req);

    }
    public void getUserInfo() {
        String url = String.format(Constants.baseUrl + "/api/Account/userInfo");
        queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                response.remove("AttendedEvents");
                response.remove("UserID");
                response.remove("Name");
                response.remove("Username");
                System.out.println("cool beans");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.networkResponse.toString());
                System.out.println("not cool beans");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "application/x-www-form-urlencoded");
                return headers;
            }

        });
    }
}
