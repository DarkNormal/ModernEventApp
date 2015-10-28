package com.lordan.mark.PosseUp.UI.SigninGroup;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lordan.mark.PosseUp.AbstractActivity;
import com.lordan.mark.PosseUp.DataOperations.AzureService;
import com.lordan.mark.PosseUp.Model.User;
import com.lordan.mark.PosseUp.R;
import com.lordan.mark.PosseUp.UI.MainActivity;
import com.lordan.mark.PosseUp.UI.RegisterActivity;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;

/**
 * Created by Mark on 10/10/2015.
 */
public class SigninFrag extends Fragment implements View.OnClickListener{
    private View detailsView;
    private TextView username;
    private MobileServiceClient mobileServiceClient = AbstractActivity.mobileServiceClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailsView = inflater.inflate(R.layout.signinfrag_layout, container, false);
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
        final TextView username = (TextView) detailsView.findViewById(R.id.username_signin);
        final ProgressDialog mProgressDialog = ProgressDialog.show(getActivity(), "Resetting password",
                "Please wait...", true);
        if(!username.getText().toString().isEmpty()){

            User forgetfulUser = new User();
            forgetfulUser.setEmail(username.getText().toString());

            ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("forgot_password",forgetfulUser, JsonElement.class);
            Futures.addCallback(result, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    if (result.getAsJsonObject().get("status").getAsString().equals("email sent")) {
                        Toast.makeText(getActivity(), "Email sent to " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

                        ForgotPasswordFrag newFragment = ForgotPasswordFrag.newInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString("email", username.getText().toString());
                        newFragment.setArguments(bundle);

                        ft.replace(R.id.fragmentHolder, newFragment, "forgotpass_frag");
                        ft.commit();
                    } else {
                        username.setError("No matching email found");
                        mProgressDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("onFailure");
                    mProgressDialog.dismiss();
                }
            });
        }
        else{
            username.setError("Enter an email address");
            mProgressDialog.dismiss();
        }

    }
    public static SigninFrag newInstance()
    {
        SigninFrag myFragment = new SigninFrag();
        return myFragment;
    }
    public void login(String username, String password) {
        final ProgressDialog mProgressDialog = ProgressDialog.show(getActivity(), "Logging in",
                "Please wait...", true);
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        ListenableFuture<JsonElement> result = mobileServiceClient.invokeApi("CustomLogin", user, JsonElement.class);

        Futures.addCallback(result, new FutureCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                if (result.isJsonObject()) {
                    JsonObject resultObj = result.getAsJsonObject();
                    if (resultObj.get("userId").getAsString().contains("custom:")) {
                        MobileServiceUser mUser = new MobileServiceUser(resultObj.get("userId").getAsString());
                        String token = resultObj.get("mobileServiceAuthenticationToken").toString();
                        token = token.replace("\"", "");
                        mUser.setAuthenticationToken(token);
                        mobileServiceClient.setCurrentUser(mUser);
                        AzureService az = new AzureService();
                        az.saveUserData(getActivity(), mobileServiceClient, user.getUsername(), user.getEmail());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        mProgressDialog.dismiss();
                    }

                } else {
                    System.out.println("dang");
                    mProgressDialog.dismiss();
                }

            }
            @Override
            public void onFailure(Throwable exc) {

                mProgressDialog.dismiss();
                if(exc.getLocalizedMessage().contains("Invalid username or password")){
                    final TextView username = (TextView) detailsView.findViewById(R.id.username_signin);
                    username.setError("Invalid username or password");
                }
                else{
                    Toast.makeText(getActivity(), "No internet connection detected", Toast.LENGTH_SHORT).show();
                }
                System.out.println("onFailure Signin User");
            }


        });
    }
}
